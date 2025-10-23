<script setup lang="ts">
import { ref } from "vue";
import { PollApi, type PollDto, type VoteOptionDto } from "../api/polls";

const question = ref("");
const options = ref<VoteOptionDto[]>([
  { id: 0, caption: "", presentationOrder: 0 },
  { id: 1, caption: "", presentationOrder: 1 },
]);

const loading = ref(false);
const error = ref<string | null>(null);
const success = ref(false);

// Emit til App.vue slik at den kan refresh poll-listen etter opprettelse
const emit = defineEmits<{
  (e: "created"): void;
}>();

function addOption() {
  options.value.push({
    id: options.value.length,
    caption: "",
    presentationOrder: options.value.length,
  });
}

function removeOption(idx: number) {
  options.value.splice(idx, 1);
}

async function createPoll() {
  if (!question.value.trim() || options.value.length < 2) {
    error.value = "Du må ha spørsmål og minst 2 alternativer";
    return;
  }

  loading.value = true;
  error.value = null;
  success.value = false;

  const dto = {
    creatorId: 1, // midlertidig
    question: question.value,
    options: options.value.map(o => ({
      caption: o.caption,
      presentationOrder: o.presentationOrder,
    })),
  };

  console.log("[CREATE POLL] payload:", dto);

  try {
    await PollApi.create(dto as any);
    success.value = true;
    emit("created");
    question.value = "";
    options.value = [
      { id: 0, caption: "", presentationOrder: 0 },
      { id: 1, caption: "", presentationOrder: 1 },
    ];
  } catch (e) {
    console.error("[CREATE POLL] error:", e);
    error.value = "Kunne ikke opprette poll (se konsoll for detaljer)";
  } finally {
    loading.value = false;
  }
}


</script>

<template>
  <div class="border rounded p-4 mb-6">
    <h2 class="text-xl font-semibold mb-3">Create New Poll</h2>

    <div v-if="error" class="text-red-600 mb-3">{{ error }}</div>
    <div v-if="success" class="text-green-700 mb-3">Poll created successfully</div>

    <div class="mb-4">
      <label class="block mb-1">Question</label>
      <input
          v-model="question"
          type="text"
          class="border p-2 rounded w-full"
          placeholder="Enter poll question"
      />
    </div>

    <div class="mb-4">
      <label class="block font-medium mb-2">Options</label>

      <div class="space-y-2">
        <div
            v-for="(opt, idx) in options"
            :key="idx"
            class="flex gap-2"
        >
          <input
              v-model="opt.caption"
              type="text"
              class="border flex-1 p-2 rounded"
              placeholder="Option text"
          />
          <button
              v-if="options.length > 2"
              @click="removeOption(idx)"
              class="px-2 border rounded hover:bg-gray-50"
          >
            X
          </button>
        </div>
      </div>

      <button
          type="button"
          class="mt-3 px-3 py-1 border rounded hover:bg-gray-50"
          @click="addOption"
      >
        + Add option
      </button>
    </div>

    <button
        @click="createPoll"
        class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        :disabled="loading"
    >
      {{ loading ? "Creating..." : "Create Poll" }}
    </button>
  </div>
</template>
