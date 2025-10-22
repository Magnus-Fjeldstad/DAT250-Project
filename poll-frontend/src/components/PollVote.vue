<script setup lang="ts">
import { ref, onMounted } from "vue";
import type { PollDto, VoteOptionDto } from "../api/polls";
import { VoteApi } from "../api/votes";
import { PollApi } from "../api/polls";

const props = defineProps<{
  poll: PollDto;
}>();

interface PollResultDto {
  optionId: number;
  caption: string;
  upVotes: number;
  downVotes: number;
  score: number;
}

const results = ref<PollResultDto[]>([]);
const sending = ref<number | null>(null);
const error = ref<string | null>(null);

async function loadResults() {
  try {
    const r = await PollApi.getResults(props.poll.id);
    results.value = Array.isArray(r) ? r : [];
  } catch (e) {
  }
}

async function vote(option: VoteOptionDto, value: number) {
  sending.value = option.id;
  error.value = null;

  try {
    await VoteApi.vote({
      pollId: props.poll.id!,
      optionId: option.id!,
      userId: 1,   // midlertidig
      value,
    });
    await loadResults();
  } catch (e) {
    error.value = "Kunne ikke sende stemme";
  } finally {
    sending.value = null;
  }
}

onMounted(loadResults);
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-4">{{ poll.question }}</h2>

    <div v-if="error" class="text-red-600 mb-4">{{ error }}</div>

    <ul class="space-y-4">
      <li
          v-for="option in poll.options"
          :key="option.id"
          class="border p-4 rounded"
      >
        <div class="flex justify-between items-center">
          <span>{{ option.caption }}</span>

          <div class="space-x-2">
            <button
                class="px-3 py-1 border rounded hover:bg-gray-50"
                @click="vote(option, 1)"
                :disabled="sending === option.id"
            >Upvote</button>

            <button
                class="px-3 py-1 border rounded hover:bg-gray-50"
                @click="vote(option, -1)"
                :disabled="sending === option.id"
            >Downvote</button>
          </div>
        </div>

        <!-- LIVE RESULTS -->
        <div class="mt-2 text-sm text-gray-700" v-for="r in results" :key="'r-'+r.optionId">
          <div v-if="r.optionId === option.id" class="flex justify-between">
            <span>Score: {{ r.score }}</span>
            <span>({{ r.upVotes }} up / {{ r.downVotes }} down)</span>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>
