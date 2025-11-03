<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { createPoll } from "../api/api.js";

const router = useRouter();

const title = ref("");
const optionInputs = ref([""]);
const error = ref("");
const success = ref("");

// add empty option field
const addOptionField = () => {
  optionInputs.value.push("");
};

// remove option field
const removeOptionField = (index) => {
  optionInputs.value.splice(index, 1);
};

// submit form
const submit = async () => {
  error.value = "";
  success.value = "";

  const options = optionInputs.value.filter(o => o.trim() !== "");

  if (!title.value.trim() || options.length < 2) {
    error.value = "Enter poll title and at least two options.";
    return;
  }

  try {
    await createPoll(title.value, options);
    success.value = "Poll created.";
    setTimeout(() => router.push("/home"), 800);
  } catch {
    error.value = "Failed to create poll.";
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <form class="w-full max-w-md space-y-4 p-6 border bg-white" @submit.prevent="submit">

      <h1 class="text-lg font-semibold">Create Poll</h1>

      <input
          v-model="title"
          placeholder="Poll title"
          class="w-full border p-2 focus:outline-none focus:ring"
      />

      <div class="space-y-2">
        <label class="text-sm font-medium">Options</label>

        <div v-for="(opt, idx) in optionInputs" :key="idx" class="flex gap-2">
          <input
              v-model="optionInputs[idx]"
              placeholder="Option"
              class="flex-1 border p-2 focus:outline-none focus:ring"
          />
          <button
              type="button"
              class="px-2 text-red-600 font-bold"
              @click="removeOptionField(idx)"
              v-if="optionInputs.length > 1"
          >
            x
          </button>
        </div>

        <button
            type="button"
            class="text-sm text-blue-600 underline"
            @click="addOptionField"
        >
          + Add option
        </button>
      </div>

      <button class="w-full py-2 bg-green-600 text-white">Create</button>

      <div v-if="error" class="text-red-600 text-sm">{{ error }}</div>
      <div v-if="success" class="text-green-600 text-sm">{{ success }}</div>

      <button
          type="button"
          class="w-full text-sm text-blue-600 underline"
          @click="router.push('/home')"
      >
        Back
      </button>
    </form>
  </div>
</template>
