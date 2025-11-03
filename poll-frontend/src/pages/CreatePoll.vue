<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { createPoll } from "../api/api.js";

const router = useRouter();

const title = ref("");
const optionInputs = ref([""]);
const loading = ref(false);
const error = ref("");
const success = ref("");

const addOptionField = () => optionInputs.value.push("");
const removeOptionField = (idx) => optionInputs.value.splice(idx, 1);

const submit = async () => {
  error.value = "";
  success.value = "";

  const opts = optionInputs.value.filter(o => o.trim() !== "");

  if (!title.value.trim() || opts.length < 2) {
    error.value = "Enter a title and at least two options.";
    return;
  }

  loading.value = true;
  try {
    await createPoll(title.value, opts);
    success.value = "Poll created!";
    setTimeout(() => router.push("/home"), 800);
  } catch {
    error.value = "Failed to create poll.";
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100 p-6 animate-fadeIn">
    <form
        @submit.prevent="submit"
        class="w-full max-w-md bg-white rounded-xl shadow-lg p-6 space-y-6 border border-gray-200"
    >

      <h1 class="text-xl font-semibold text-gray-800 text-center">
        Create a New Poll
      </h1>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Poll title</label>
        <input
            v-model="title"
            placeholder="What do you want to ask?"
            class="w-full border rounded-lg p-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-gray-50"
        />
      </div>

      <div>
        <label class="text-sm font-medium text-gray-700 mb-1 block">Options</label>

        <div class="space-y-2">
          <div v-for="(opt, idx) in optionInputs" :key="idx" class="flex gap-2">
            <input
                v-model="optionInputs[idx]"
                placeholder="Option"
                class="flex-1 border rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50"
            />

            <button
                v-if="optionInputs.length > 1"
                type="button"
                @click="removeOptionField(idx)"
                class="px-2 py-1 text-xs bg-red-100 text-red-700 rounded hover:bg-red-200 transition"
            >
              ✕
            </button>
          </div>

          <button
              type="button"
              @click="addOptionField"
              class="text-sm text-blue-600 hover:text-blue-700 font-medium transition"
          >
            + Add option
          </button>
        </div>
      </div>

      <button
          :disabled="loading"
          class="w-full py-2.5 rounded-lg text-white font-semibold transition
               bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400"
      >
        {{ loading ? "Creating..." : "Create poll" }}
      </button>

      <div v-if="error" class="text-sm text-red-600 text-center">{{ error }}</div>
      <div v-if="success" class="text-sm text-green-600 text-center">{{ success }}</div>

      <button
          type="button"
          class="w-full text-sm text-gray-600 hover:text-gray-800 transition font-medium"
          @click="router.push('/home')"
      >
        ← Back
      </button>
    </form>
  </div>
</template>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to   { opacity: 1; transform: translateY(0); }
}
.animate-fadeIn {
  animation: fadeIn .25s ease-out;
}
</style>
