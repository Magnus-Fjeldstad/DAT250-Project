<script setup>
import api from "../api/api.js";
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import PollList from "../components/PollList.vue";

const router = useRouter();
const user = ref(null);
const polls = ref([]);
let intervalId = null;

const loadUser = async () => {
  const res = await api.get("/auth/me");
  user.value = res.data;
};

const loadPolls = async () => {
  try {
    const res = await api.get("/polls");
    polls.value = res.data;
  } catch {
    console.error("Failed to fetch polls");
  }
};

onMounted(async () => {
  await loadUser();
  await loadPolls();
  intervalId = setInterval(loadPolls, 5000);
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});

const logout = async () => {
  await api.post("/auth/logout");
  router.push("/");
};
</script>

<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <header class="bg-white shadow-sm border-b">
      <div class="max-w-4xl mx-auto px-6 py-4 flex justify-between items-center">
        <h1 class="text-xl font-semibold text-gray-800">Poll Dashboard</h1>

        <div class="flex items-center gap-3">
          <span class="text-sm text-gray-600">
            Logged in as <strong>{{ user?.username }}</strong>
          </span>

          <button
              @click="router.push('/create-poll')"
              class="px-3 py-1.5 text-sm bg-blue-600 hover:bg-blue-700 text-white rounded-md transition"
          >
            New Poll
          </button>

          <button
              v-if="user?.role === 'ROLE_ADMIN'"
              @click="router.push('/admin')"
              class="px-3 py-1.5 text-sm bg-red-600 hover:bg-red-700 text-white rounded-md transition"
          >
            Admin
          </button>

          <button
              @click="router.push('/my-polls')"
              class="px-3 py-1.5 text-sm bg-purple-600 hover:bg-purple-700 text-white rounded-md transition"
          >
            My Polls
          </button>

          <button
              @click="logout"
              class="px-3 py-1.5 text-sm bg-gray-800 hover:bg-black text-white rounded-md transition"
          >
            Logout
          </button>
        </div>
      </div>
    </header>

    <main class="flex-1 max-w-4xl mx-auto w-full px-6 py-8">
      <h2 class="text-lg font-medium mb-4 text-gray-800">Available Polls</h2>
      <div class="bg-white rounded-lg shadow-sm border p-4">
        <PollList :polls="polls" />
      </div>
    </main>
  </div>
</template>
