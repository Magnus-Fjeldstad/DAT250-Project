<script setup>
// MyPolls.vue
// Shows only the current user's polls, with delete support

import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../api/api.js"; // axios w/ cookie session
import { getPolls } from "../api/api.js";

const router = useRouter();

const user = ref(null);
const polls = ref([]);
const loading = ref(true);
const error = ref("");
const deleting = ref({}); // track delete state

// Load current session user + polls
const load = async () => {
  error.value = "";
  try {
    const me = await api.get("/auth/me");
    user.value = me.data;

    const res = await getPolls();
    // Filter to only polls created by current user
    polls.value = (res.data || []).filter(p => p.creatorId === user.value.id);
  } catch {
    // If not authed, send to login
    router.push("/");
  } finally {
    loading.value = false;
  }
};

// Delete a poll
const deletePoll = async (id) => {
  deleting.value[id] = true;
  try {
    await api.delete(`/polls/${id}`);
    polls.value = polls.value.filter(p => p.id !== id);
  } catch {
    error.value = "Failed to delete poll.";
  } finally {
    deleting.value[id] = false;
  }
};

onMounted(load);
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">

    <!-- Header -->
    <header class="bg-white shadow-sm border-b">
      <div class="max-w-4xl mx-auto px-6 py-4 flex justify-between items-center">
        <h1 class="text-xl font-semibold text-gray-800">
          My Polls
        </h1>

        <div class="flex items-center gap-3 text-sm">
          <span class="text-gray-600">Logged in as <strong>{{ user?.username }}</strong></span>

          <button
              @click="router.push('/home')"
              class="px-3 py-1.5 bg-gray-200 hover:bg-gray-300 rounded-md"
          >
            Home
          </button>
        </div>
      </div>
    </header>

    <!-- Main content -->
    <main class="flex-1 max-w-4xl mx-auto w-full px-6 py-8">

      <div v-if="loading" class="text-gray-500 text-sm">Loading...</div>
      <div v-if="error" class="text-red-600 text-sm mb-2">{{ error }}</div>

      <div v-if="polls.length === 0 && !loading" class="text-sm text-gray-500">
        You have not created any polls yet.
      </div>

      <ul class="space-y-3">
        <li
            v-for="poll in polls"
            :key="poll.id"
            class="bg-white border rounded-lg p-4 shadow-sm"
        >
          <div class="flex justify-between items-center">
            <div>
              <div class="font-semibold text-gray-800">{{ poll.question }}</div>
              <div class="text-xs text-gray-500 mt-1">ID: {{ poll.id }}</div>
            </div>

            <button
                @click="deletePoll(poll.id)"
                :disabled="deleting[poll.id]"
                class="px-3 py-1.5 text-sm bg-red-600 hover:bg-red-700 text-white rounded-md transition disabled:bg-red-400"
            >
              {{ deleting[poll.id] ? "Deleting..." : "Delete" }}
            </button>
          </div>
        </li>
      </ul>

    </main>
  </div>
</template>
