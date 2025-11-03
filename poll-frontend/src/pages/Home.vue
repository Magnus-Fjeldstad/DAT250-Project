<script setup>
import api from "../api/api.js";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import PollList from "../Components/PollList.vue";

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

      <div class="flex gap-2">
        <button
            @click="router.push('/create-poll')"
            class="px-3 py-1 bg-blue-600 text-white"
        >
          New Poll
        </button>

        <button
            @click="logout"
            class="px-3 py-1 bg-gray-800 text-white"
        >
          Logout
        </button>
      </div>
    </div>

    <PollList />
  </div>
</template>
