<script setup>
// import reactive utilities
import { ref, onMounted } from "vue";

// import poll API
import { getPolls } from "../api/api.js";

// router
import { useRouter } from "vue-router";

const router = useRouter();

// state
const polls = ref([]);
const loading = ref(true);
const error = ref("");
const expandedPollId = ref(null);

// load polls
const loadPolls = async () => {
  try {
    const res = await getPolls();
    polls.value = res.data;
  } catch {
    error.value = "Failed to load polls";
  } finally {
    loading.value = false;
  }
};

// toggle expansion
const togglePoll = (id) => {
  expandedPollId.value = expandedPollId.value === id ? null : id;
};

// run on page load
onMounted(loadPolls);
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">

    <!-- Top bar -->
    <header class="bg-white shadow-sm border-b">
      <div class="max-w-4xl mx-auto px-6 py-4 flex justify-between items-center">
        <h1 class="text-xl font-semibold text-gray-800">
          Public Polls
        </h1>
        <button
            @click="router.push('/')"
            class="px-3 py-1.5 text-sm bg-blue-600 hover:bg-blue-700 text-white rounded-md"
        >
          Login
        </button>
      </div>
    </header>

    <!-- Guest info -->
    <div class="bg-yellow-50 border-b border-yellow-200 text-sm text-yellow-800 py-2 text-center">
      You are viewing polls as a guest. Login to vote.
    </div>

    <!-- Body -->
    <main class="flex-1 max-w-4xl mx-auto w-full px-6 py-8">

      <h2 class="text-lg font-medium mb-4 text-gray-800">Available Polls</h2>

      <div v-if="loading" class="text-gray-500">Loading polls...</div>
      <div v-if="error" class="text-red-600 text-sm mb-3">{{ error }}</div>

      <ul class="space-y-3" v-if="!loading">
        <li
            v-for="poll in polls"
            :key="poll.id"
            class="rounded-lg border bg-white shadow-sm hover:shadow-md transition cursor-pointer overflow-hidden"
        >
          <!-- Header -->
          <div
              class="flex justify-between items-center px-4 py-3 hover:bg-gray-50"
              @click="togglePoll(poll.id)"
          >
            <div>
              <div class="font-semibold text-gray-800 text-sm sm:text-base">
                {{ poll.question }}
              </div>
              <div class="text-xs text-gray-500">ID: {{ poll.id }}</div>
            </div>

            <svg
                :class="[
                'w-4 h-4 text-gray-500 transition-transform',
                expandedPollId === poll.id ? 'rotate-180' : ''
              ]"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round"
                    d="M19 9l-7 7-7-7" />
            </svg>
          </div>

          <!-- Options preview -->
          <transition
              enter-active-class="transition ease-out duration-200"
              enter-from-class="opacity-0 -translate-y-2"
              enter-to-class="opacity-100 translate-y-0"
              leave-active-class="transition ease-in duration-150"
              leave-from-class="opacity-100 translate-y-0"
              leave-to-class="opacity-0 -translate-y-2"
          >
            <div v-if="expandedPollId === poll.id" class="p-4 border-t bg-gray-50 space-y-2 text-sm text-gray-700">
              <p class="font-medium mb-2">Options:</p>

              <ul class="list-disc ml-4">
                <li v-for="opt in poll.options" :key="opt.id">
                  {{ opt.caption }}
                </li>
              </ul>

              <p class="text-xs text-gray-500 mt-2">
                Login to vote on this poll.
              </p>
            </div>
          </transition>
        </li>
      </ul>

      <p
          v-if="!loading && polls.length === 0"
          class="text-sm text-gray-500 text-center mt-6"
      >
        No polls found.
      </p>
    </main>

  </div>
</template>
