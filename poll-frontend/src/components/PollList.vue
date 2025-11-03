<script setup>
import { ref, onMounted } from "vue";
import { getPolls } from "../api/api.js";
import PollVote from "./PollVote.vue";

const polls = ref([]);
const loading = ref(true);
const error = ref("");
const expandedPollId = ref(null);

onMounted(async () => {
  try {
    const res = await getPolls();
    polls.value = res.data;
  } catch {
    error.value = "Failed to load polls";
  } finally {
    loading.value = false;
  }
});

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
          v-for="poll in polls"
          :key="poll.id"
          class="rounded-lg border bg-white shadow-sm hover:shadow-md transition cursor-pointer overflow-hidden"
      >
        <!-- Poll Header -->
        <div
            class="flex justify-between items-center px-4 py-3 hover:bg-gray-50 transition"
            @click="togglePoll(poll.id)"
        >
          <div>
            <div class="font-semibold text-gray-800 text-sm sm:text-base">
              {{ poll.question }}
            </div>
            <div class="text-xs text-gray-500 mt-0.5">
              ID: {{ poll.id }}
            </div>
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
            <path stroke-linecap="round" stroke-linejoin="round"
                  d="M19 9l-7 7-7-7"/>
          </svg>
        </div>

        <!-- Inline Poll Voting -->
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
        v-if="!loading && polls.length === 0"
        class="text-sm text-gray-500 text-center mt-6"
    >
      No polls found. Create one above!
    </p>

  </div>
</template>
