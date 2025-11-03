<!-- src/components/PollList.vue -->
<template>
  <div class="list">
    <div class="line" v-for="p in polls" :key="p.id">
      <div class="left">
        <h3>{{ p.question }}</h3>
        <small>Creator ID: {{ p.creatorId ?? "n/a" }}</small>
      </div>

      <div class="right">
        <!-- Guests: no create/update/delete; they can still click to view or see results elsewhere -->
        <!-- Users: can update/delete only own polls -->
        <!-- Admins: can update/delete any poll -->
        <button v-if="canEdit(p)" @click="$emit('edit', p)">Edit</button>
        <button v-if="canDelete(p)" @click="del(p.id!)">Delete</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { PollApi, type PollDto } from "../api/polls";
import { useAuth } from "../composables/useAuth";

const emits = defineEmits<{ (e: "edit", poll: PollDto): void }>();
const auth = useAuth();
const polls = ref<PollDto[]>([]);

function canEdit(p: PollDto) {
  // Admin can edit any; User only own; Guest none
  return auth.hasRole("ADMIN") || auth.isOwner(p.creatorId || null);
}
function canDelete(p: PollDto) { return canEdit(p); }

async function load() { polls.value = await PollApi.getAll(); }
async function del(id: number) {
  if (!confirm("Delete this poll?")) return;
  await PollApi.delete(id);
  await load();
}

onMounted(load);
</script>

<style scoped>
.list { display:grid; gap:.75rem; }
.line { display:flex; justify-content:space-between; align-items:center; border:1px solid #eee; border-radius:8px; padding:.6rem .8rem; }
.left h3 { margin:0; }
.right button { margin-left:.5rem; padding:.35rem .6rem; border-radius:6px; cursor:pointer; }
</style>
