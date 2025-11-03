<!-- src/pages/Home.vue -->
<script setup>
// checks session guard via router; offers logout
import api from "../api";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const user = ref(null);

onMounted(async () => {
  const res = await api.get("/auth/me");
  user.value = res.data;
});

const logout = async () => {
  await api.post("/auth/logout");
  router.push("/");
};
</script>

<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-lg font-semibold">Welcome {{ user?.username }}</h2>
      <button @click="logout" class="px-3 py-1 bg-gray-800 text-white">Logout</button>
    </div>
    <p>Authenticated content here</p>
  </div>
</template>
