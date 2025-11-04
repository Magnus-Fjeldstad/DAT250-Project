<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getPolls, deletePoll } from "../api/api.js";

const router = useRouter();
const polls = ref([]);
const loading = ref(true);
const error = ref("");

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

const remove = async (id) => {
  if (!confirm("Delete this poll?")) return;
  await deletePoll(id);
  polls.value = polls.value.filter(p => p.id !== id);
};

onMounted(loadPolls);
</script>

<template>
  <div class="min-h-screen bg-gray-50 p-6 flex flex-col">
    <div class="max-w-3xl mx-auto w-full">

      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-semibold text-gray-800">Admin Console</h1>

        <button
            @click="router.push('/home')"
            class="px-3 py-1.5 text-sm bg-gray-800 hover:bg-black text-white rounded-md"
        >
          Back to Home
        </button>
      </div>

      <div class="bg-white shadow-sm border rounded-lg p-4">
        <h2 class="text-lg font-medium mb-4 text-gray-800">All Polls</h2>

        <div v-if="loading" class="text-gray-500">Loading...</div>
        <div v-if="error" class="text-red-600">{{ error }}</div>

        <ul class="space-y-2">
          <li
              v-for="poll in polls"
              :key="poll.id"
              class="flex justify-between items-center border rounded p-3 bg-gray-50"
          >
            <div>
              <div class="font-medium">{{ poll.question }}</div>
              <div class="text-xs text-gray-500">ID: {{ poll.id }}</div>
            </div>

            <button
                @click="remove(poll.id)"
                class="px-3 py-1 text-sm bg-red-600 hover:bg-red-700 text-white rounded"
            >
              Delete
            </button>
          </li>
        </ul>

        <p v-if="!loading && polls.length === 0"
           class="text-sm text-gray-500 mt-4 text-center">
          No polls found
        </p>

      </div>
    </div>
  </div>
</template>
