<!-- src/components/UserPanel.vue -->
<template>
  <div class="bar" v-if="auth.state.user">
    <span>Logged in as <strong>{{ auth.state.user.username }}</strong> ({{ auth.state.user.role }})</span>
    <button @click="onLogout" :disabled="auth.state.loading">
      {{ auth.state.loading ? "Logging out..." : "Logout" }}
    </button>
  </div>
</template>

<script setup lang="ts">
import { useAuth } from "../composables/useAuth";
const auth = useAuth();
async function onLogout() { await auth.logout(); }
</script>

<style scoped>
.bar { display: flex; justify-content: space-between; align-items: center; padding: .5rem .75rem; border: 1px solid #ddd; border-radius: 8px; margin-bottom: 1rem; }
button { padding: .35rem .65rem; border-radius: 6px; cursor: pointer; }
</style>
