<template>
  <div class="login-card">
    <h2>Login</h2>
    <form @submit.prevent="onSubmit">
      <label>
        Username
        <input v-model="username" autocomplete="username" />
      </label>
      <label>
        Password
        <input v-model="password" type="password" autocomplete="current-password" />
      </label>
      <button :disabled="auth.state.loading" type="submit">
        {{ auth.state.loading ? "Logging in..." : "Login" }}
      </button>
      <p v-if="auth.state.error" class="error">{{ auth.state.error }}</p>
    </form>

    <hr />

    <h3>Or Register</h3>
    <form @submit.prevent="onRegister">
      <label>
        Username
        <input v-model="rUsername" />
      </label>
      <label>
        Email
        <input v-model="rEmail" type="email" />
      </label>
      <label>
        Password
        <input v-model="rPassword" type="password" />
      </label>
      <button :disabled="auth.state.loading" type="submit">
        {{ auth.state.loading ? "Registering..." : "Register" }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
// Simple login+register form that uses the session cookie flow
import { ref, onMounted } from "vue";
import { useAuth } from "../composables/useAuth";

const auth = useAuth();

const username = ref("");
const password = ref("");
const rUsername = ref("");
const rEmail = ref("");
const rPassword = ref("");

async function onSubmit() {
  await auth.login({ username: username.value, password: password.value });
}

async function onRegister() {
  await auth.register({ username: rUsername.value, email: rEmail.value, password: rPassword.value });
}

// When mounted, attempt to fetch session user if exists
onMounted(() => {
  auth.refreshMe();
});
</script>

<style scoped>
.login-card { max-width: 360px; margin: 2rem auto; display: grid; gap: .75rem; }
label { display: grid; gap: .25rem; }
input { padding: .5rem; border: 1px solid #ccc; border-radius: 6px; }
button { padding: .5rem .75rem; border-radius: 6px; cursor: pointer; }
.error { color: #b00; }
</style>
