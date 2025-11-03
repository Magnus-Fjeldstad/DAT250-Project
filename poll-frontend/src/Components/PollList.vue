<script setup>
// poll list component
import { ref, onMounted } from "vue";
import { getPolls } from "../api/api.js";
import { useRouter } from "vue-router";

const polls = ref([]);
const loading = ref(true);
const error = ref("");
const router = useRouter();

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

// navigate to poll voting (we will implement vote page later)
const goToPoll = (id) => {
  router.push(`/poll/${id}`);
};
</script>

<template>
  <div class="mt-6">
    <h3 class="text-md font-semibold mb-2">Available Polls</h3>

    <div v-if="loading">Loading polls...</div>
    <div v-if="error" class="text-red-600 text-sm">{{ error }}</div>

    <ul class="space-y-2">
      <li
          v-for="poll in polls"
          :key="poll.id"
          class="border p-3 cursor-pointer hover:bg-gray-100"
          @click="goToPoll(poll.id)"
      >
        <div class="font-medium">{{ poll.question }}</div>
        <div class="text-sm text-gray-500">ID: {{ poll.id }}</div>
      </li>
    </ul>

    <p v-if="!loading && polls.length === 0" class="text-sm text-gray-500 mt-2">
      No polls found.
    </p>
  </div>
</template>
