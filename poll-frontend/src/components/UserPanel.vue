<template>
  <div class="user-panel" v-if="auth.state.user">
    <p>Logged in as <strong>{{ auth.state.user.username }}</strong> ({{ auth.state.user.role }})</p>
    <button @click="onLogout" :disabled="auth.state.loading">
      {{ auth.state.loading ? "Logging out..." : "Logout" }}
    </button>
  </div>
</template>

<script setup lang="ts">
// Displays current user and lets you logout
import { useAuth } from "../composables/useAuth";
const auth = useAuth();

async function onLogout() {
  await auth.logout();
}
</script>

<style scoped>
.user-panel { display: flex; align-items: center; gap: 1rem; padding: .5rem 0; }
button { padding: .4rem .7rem; border-radius: 6px; cursor: pointer; }
</style>
