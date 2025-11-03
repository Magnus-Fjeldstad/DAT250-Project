<!-- src/components/PollVote.vue -->
<template>
  <div class="card" v-if="pollId">
    <h2>Vote</h2>

    <div v-if="!auth.isAuthenticated()" class="hint">
      You must be logged in to vote. Guests can only view totals.
    </div>

    <ul class="options">
      <li v-for="opt in options" :key="opt.id" class="row">
        <span>{{ opt.caption }}</span>
        <div class="actions">
          <!-- Disable vote buttons for guests -->
          <button :disabled="!auth.isAuthenticated()" @click="cast(opt.id!, 1)">Up</button>
          <button :disabled="!auth.isAuthenticated()" @click="cast(opt.id!, -1)">Down</button>
          <button :disabled="!auth.isAuthenticated()" @click="cast(opt.id!, 0)">Clear</button>
        </div>
      </li>
    </ul>

    <h3 class="mt">Results</h3>
    <table class="tbl">
      <thead><tr><th>Option</th><th>Up</th><th>Down</th></tr></thead>
      <tbody>
      <tr v-for="r in results" :key="r.optionId">
        <td>{{ r.caption }}</td>
        <td>{{ r.upVotes }}</td>
        <td>{{ r.downVotes }}</td>
      </tr>
      </tbody>
    </table>

    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { PollApi, type PollDto } from "../api/polls";
import { VoteApi } from "../api/votes";
import { useAuth } from "../composables/useAuth";

const props = defineProps<{ pollId: number }>();

const auth = useAuth();
const poll = ref<PollDto | null>(null);
const options = ref<{ id: number; caption: string }[]>([]);
const results = ref<{ optionId: number; caption: string; upVotes: number; downVotes: number }[]>([]);
const error = ref<string | null>(null);

async function load() {
  error.value = null;
  try {
    poll.value = await PollApi.getById(props.pollId);
    options.value = (poll.value.options || []).map(o => ({ id: o.id!, caption: o.caption }));
    results.value = await PollApi.getResults(props.pollId);
  } catch (e: any) { error.value = e.message || "Failed to load"; }
}

async function cast(optionId: number, value: -1 | 0 | 1) {
  error.value = null;
  try {
    await VoteApi.vote({ optionId, value });
    // Refresh totals after any change
    results.value = await PollApi.getResults(props.pollId);
  } catch (e: any) { error.value = e.message || "Vote failed"; }
}

onMounted(load);
watch(() => props.pollId, load);
</script>

<style scoped>
.card { border:1px solid #ddd; border-radius:10px; padding:1rem; display:grid; gap:.6rem; }
.hint { background:#fffbeb; border:1px solid #f3e5ab; padding:.5rem .6rem; border-radius:6px; }
.options { list-style:none; padding:0; display:grid; gap:.5rem; }
.row { display:flex; justify-content:space-between; align-items:center; }
.actions button { margin-left:.4rem; padding:.35rem .6rem; border-radius:6px; cursor:pointer; }
.tbl { border-collapse:collapse; width:100%; }
.tbl th, .tbl td { border:1px solid #eee; padding:.4rem .5rem; text-align:left; }
.mt { margin-top:.5rem; }
.error { color:#a00; }
</style>
