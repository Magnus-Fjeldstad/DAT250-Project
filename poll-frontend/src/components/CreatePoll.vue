<!-- src/components/CreatePoll.vue -->
<template>
  <div class="card">
    <h2>Create poll</h2>
    <form @submit.prevent="onCreate">
      <label>Question <input v-model="question" /></label>

      <div class="options">
        <div v-for="(opt, i) in options" :key="i" class="opt">
          <input v-model="opt.caption" placeholder="Option caption" />
          <input v-model.number="opt.presentationOrder" type="number" min="0" style="width:6rem" />
          <button type="button" @click="remove(i)">Remove</button>
        </div>
      </div>

      <div class="row">
        <button type="button" @click="add()">Add option</button>
        <span class="spacer"></span>
        <button :disabled="creating" type="submit">{{ creating ? "Creating..." : "Create" }}</button>
      </div>
    </form>

    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { PollApi, type PollDto, type VoteOptionDto } from "../api/polls";
import { useAuth } from "../composables/useAuth";

const auth = useAuth(); // Creation requires authentication server-side

const question = ref("");
const options = ref<VoteOptionDto[]>([
  { caption: "Yes", presentationOrder: 0 },
  { caption: "No",  presentationOrder: 1 },
]);

const creating = ref(false);
const error = ref<string | null>(null);

function add() { options.value.push({ caption: "", presentationOrder: options.value.length }); }
function remove(i: number) { options.value.splice(i, 1); }

async function onCreate() {
  error.value = null; creating.value = true;
  try {
    const dto: PollDto = { question: question.value, options: options.value };
    await PollApi.create(dto); // creator is taken from session in backend
    question.value = ""; options.value = [];
  } catch (e: any) {
    error.value = e.message || "Failed to create poll";
  } finally { creating.value = false; }
}
</script>

<style scoped>
.card { border:1px solid #ddd; border-radius:10px; padding:1rem; display:grid; gap:.6rem; }
label { display:grid; gap:.25rem; }
input { padding:.5rem; border:1px solid #ccc; border-radius:6px; }
.options { display:grid; gap:.5rem; }
.opt { display:flex; gap:.5rem; align-items:center; }
.row { display:flex; align-items:center; gap:.5rem; }
.spacer { flex:1; }
button { padding:.4rem .7rem; border-radius:6px; cursor:pointer; }
.error { color:#a00; }
</style>
