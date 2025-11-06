<script setup>
import { ref, onMounted, watch } from "vue";
import { getPolls } from "../api/api.js";
import PollVote from "./PollVote.vue";

// If parent passes polls, use them
const props = defineProps({
  polls: { type: Array, default: null }
});

const localPolls = ref([]);
const loading = ref(true);
const error = ref("");
const expandedPollId = ref(null);

// Fetch only if parent did not provide polls
const loadPollsIfNeeded = async () => {
  if (props.polls !== null) {
    localPolls.value = props.polls;
    loading.value = false;
    return;
  }

  try {
    const res = await getPolls();
    localPolls.value = res.data;
  } catch {
    error.value = "Failed to load polls";
  } finally {
    loading.value = false;
  }
};

// If parent updates polls, sync
watch(
    () => props.polls,
    (newVal) => {
      if (newVal !== null) {
        localPolls.value = newVal;
      }
    }
);

onMounted(loadPollsIfNeeded);

const togglePoll = (id) => {
  expandedPollId.value = expandedPollId.value === id ? null : id;
};
</script>

<template>
  <div class="space-y-4">
    <div v-if="loading" class="text-gray-500">Loading polls...</div>
    <div v-if="error" class="text-red-600 text-sm">{{ error }}</div>

    <ul class="space-y-3">
      <li
          v-for="poll in localPolls"
          :key="poll.id"
          class="rounded-lg border bg-white shadow-sm hover:shadow-md transition cursor-pointer overflow-hidden"
      >
        <div
            class="flex justify-between items-center px-4 py-3 hover:bg-gray-50 transition"
            @click="togglePoll(poll.id)"
        >
          <div>
            <div class="font-semibold text-gray-800 text-sm sm:text-base">
              {{ poll.question }}
            </div>
            <div class="text-xs text-gray-500 mt-0.5">ID: {{ poll.id }}</div>
          </div>

          <svg
              :class="[
              'w-4 h-4 text-gray-500 transition-transform',
              expandedPollId === poll.id ? 'rotate-180' : ''
            ]"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7"/>
          </svg>
        </div>

        <transition
            enter-active-class="transition ease-out duration-200"
            enter-from-class="opacity-0 transform -translate-y-2"
            enter-to-class="opacity-100 transform translate-y-0"
            leave-active-class="transition ease-in duration-150"
            leave-from-class="opacity-100 transform translate-y-0"
            leave-to-class="opacity-0 transform -translate-y-2"
        >
          <div v-if="expandedPollId === poll.id" class="p-4 border-t bg-gray-50">
            <PollVote :pollId="poll.id" />
          </div>
        </transition>
      </li>
    </ul>

    <p
        v-if="!loading && localPolls.length === 0"
        class="text-sm text-gray-500 text-center mt-6"
    >
      No polls found. Create one above!
    </p>
  </div>
</template>
