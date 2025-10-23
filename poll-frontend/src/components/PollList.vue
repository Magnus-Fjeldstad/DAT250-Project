<script setup lang="ts">
import { ref, onMounted } from "vue";
import { PollApi, type PollDto } from "../api/polls";

const polls = ref<PollDto[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

// Når bruker klikker en poll, gir vi det videre via emit
const emit = defineEmits<{
  (e: "select", poll: PollDto): void;
}>();

onMounted(async () => {
  try {
    polls.value = await PollApi.getAll();
  } catch (e) {
    error.value = "Kunne ikke hente polls";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="p-4">
    <h2 class="text-2xl font-semibold mb-4">Available Polls</h2>

    <div v-if="loading">Loading…</div>
    <div v-else-if="error" class="text-red-600">{{ error }}</div>
    <div v-else>
      <ul class="space-y-3">
        <li
            v-for="poll in polls"
            :key="poll.id"
            class="border p-4 rounded hover:bg-gray-50 cursor-pointer"
            @click="emit('select', poll)"
        >
          {{ poll.question }}
        </li>
      </ul>
    </div>
  </div>
</template>
