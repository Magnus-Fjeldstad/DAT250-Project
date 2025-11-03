<script setup>
// Poll voting component with WebSocket live score updates + optimistic UI
import { ref, onMounted, onUnmounted, watch } from "vue";
import {
  fetchCurrentUser,
  getPoll,
  getVotesByPoll,
  voteOnPoll
} from "../api/api.js";
import SockJS from "sockjs-client/dist/sockjs.js";
import { Client } from "@stomp/stompjs";

const props = defineProps({
  pollId: { type: Number, required: true }
});

const poll = ref(null);
const user = ref(null);
const scores = ref({});
const voted = ref({});
const anim = ref({});
const error = ref("");

let stomp = null;
let refreshInterval = null;

// Helpers
function computeScores(votes) {
  const map = {};
  votes.forEach(v => {
    map[v.optionId] = (map[v.optionId] || 0) + v.value;
    if (v.userId === user.value.id) {
      voted.value[v.optionId] = v.value;
    }
  });
  return map;
}

// Load poll + votes
const load = async () => {
  try {
    const me = await fetchCurrentUser();
    user.value = me.data;

    const p = await getPoll(props.pollId);
    poll.value = p.data;

    const votes = (await getVotesByPoll(props.pollId)).data || [];
    scores.value = computeScores(votes);
  } catch {
    error.value = "Unable to load poll";
  }
};

// Animate score bump
function bump(id) {
  anim.value[id] = true;
  setTimeout(() => (anim.value[id] = false), 250);
}

// Vote handler
const vote = async (optionId, value) => {
  const current = voted.value[optionId] || 0;
  const newVal = current === value ? 0 : value;

  const oldScore = scores.value[optionId] || 0;
  scores.value[optionId] = oldScore - current + newVal;
  voted.value[optionId] = newVal;
  bump(optionId);

  try {
    await voteOnPoll(poll.value.id, optionId, user.value.id, newVal);
  } catch {
    scores.value[optionId] = oldScore;
    voted.value[optionId] = current;
    error.value = "Vote failed";
  }
};

function connectWS() {
  const socket = new SockJS("http://localhost:8080/ws");
  stomp = new Client({
    webSocketFactory: () => socket,
    debug: () => {}
  });

  stomp.onConnect = () => {
    stomp.subscribe(`/topic/poll/${props.pollId}`, msg => {
      const votes = JSON.parse(msg.body);
      scores.value = computeScores(votes);
    });
  };

  stomp.activate();
}


// Lifecycle
onMounted(async () => {
  await load();

  try {
    connectWS();
  } catch {
    // fallback polling
    refreshInterval = setInterval(load, 3000);
  }
});

onUnmounted(() => {
  if (stomp) stomp.deactivate();
  if (refreshInterval) clearInterval(refreshInterval);
});

watch(() => props.pollId, load);
</script>

<template>
  <div v-if="poll" class="mt-2 p-3 border rounded bg-gray-50 animate-enter">
    <div class="space-y-2">
      <div
          v-for="opt in poll.options"
          :key="opt.id"
          class="flex items-center justify-between border p-2 bg-white"
      >
        <span class="font-medium text-sm">{{ opt.caption }}</span>

        <div class="flex items-center gap-3">
          <!-- Score with pop animation -->
          <transition name="score">
            <span
                :key="scores[opt.id]"
                class="text-sm font-bold transition-all duration-150"
                :class="anim[opt.id] ? 'scale-125' : ''"
            >
              {{ scores[opt.id] ?? 0 }}
            </span>
          </transition>

          <div class="flex gap-1">
            <button
                @click="vote(opt.id, 1)"
                class="px-2 py-1 text-xs border rounded font-bold transition
              hover:bg-green-600 hover:text-white
              active:scale-95"
                :class="voted[opt.id] === 1 ? 'bg-green-600 text-white' : ''"
            >
              ▲
            </button>
            <button
                @click="vote(opt.id, -1)"
                class="px-2 py-1 text-xs border rounded font-bold transition
              hover:bg-red-600 hover:text-white
              active:scale-95"
                :class="voted[opt.id] === -1 ? 'bg-red-600 text-white' : ''"
            >
              ▼
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="error" class="text-red-600 text-xs mt-2">{{ error }}</div>
  </div>
</template>

<style scoped>
@keyframes enter {
  from { opacity: 0; transform: translateY(-4px); }
  to   { opacity: 1; transform: translateY(0); }
}
.animate-enter {
  animation: enter .25s ease-out;
}

.score-enter-active,
.score-leave-active {
  transition: transform .25s, opacity .25s;
}
.score-enter-from {
  opacity: 0;
  transform: scale(0.8);
}
.score-leave-to {
  opacity: 0;
  transform: scale(1.2);
}
</style>
