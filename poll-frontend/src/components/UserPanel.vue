<script setup lang="ts">
import { ref, onMounted } from "vue";
import { UserApi, type User } from "../api/users";

const users = ref<User[]>([]);
const username = ref("");
const activeUserId = ref<number | null>(null);
const error = ref<string | null>(null);

const emit = defineEmits<{
  (e: "select-user", id: number): void;
}>();

async function loadUsers() {
  try {
    users.value = await UserApi.getAll();
  } catch (e) {
    error.value = "Kunne ikke hente brukere";
  }
}

async function createUser() {
  if (!username.value.trim()) return;

  try {
    const newUser = await UserApi.create({ username: username.value });
    users.value.push(newUser);
    activeUserId.value = newUser.id!;
    emit("select-user", newUser.id!);
    username.value = "";
  } catch (e) {
    error.value = "Feil ved opprettelse av bruker";
  }
}

function selectUser(id: number) {
  activeUserId.value = id;
  emit("select-user", id);
}

onMounted(loadUsers);
</script>

<template>
  <div class="border rounded p-4 mb-6">
    <h2 class="text-lg font-semibold mb-3">Users</h2>

    <div v-if="error" class="text-red-600 mb-2">{{ error }}</div>

    <div class="mb-4 flex gap-2">
      <input
          v-model="username"
          type="text"
          class="border p-2 rounded flex-1"
          placeholder="Enter new username"
      />
      <button
          class="px-3 py-2 border rounded bg-blue-600 text-white hover:bg-blue-700"
          @click="createUser"
      >
        Add
      </button>
    </div>

    <ul class="space-y-2">
      <li
          v-for="u in users"
          :key="u.id"
          class="flex justify-between items-center px-2 py-1 border rounded"
      >
        <span>{{ u.username }} (id: {{ u.id }})</span>
        <button
            class="text-sm px-3 py-1 rounded border"
            :class="activeUserId === u.id ? 'bg-blue-100 border-blue-500' : 'hover:bg-gray-50'"
            @click="selectUser(u.id!)"
        >Select</button>
      </li>
    </ul>
  </div>
</template>
