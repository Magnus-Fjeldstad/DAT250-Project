<template>
  <main>
    <h1>Poll App</h1>
    <UserPanel />
    <LoginForm v-if="!auth.state.user" />
    <section v-else>
      <!-- put your existing components here when authenticated -->
      <p>Authenticated. You can now access protected APIs.</p>
    </section>
  </main>
</template>

<script setup lang="ts">
// Root app that shows login or main content based on session user
import { useAuth } from "./composables/useAuth";
import LoginForm from "./components/LoginForm.vue";
import UserPanel from "./components/UserPanel.vue";

const auth = useAuth();
</script>

<style>
main { max-width: 720px; margin: 2rem auto; padding: 0 1rem; }
</style>
