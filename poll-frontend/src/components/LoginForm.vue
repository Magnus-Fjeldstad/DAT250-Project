<!-- src/components/LoginForm.vue -->
<template>
  <div class="card">
    <h2>Sign in</h2>
    <form @submit.prevent="onLogin">
      <label>Username <input v-model="username" autocomplete="username" /></label>
      <label>Password <input v-model="password" type="password" autocomplete="current-password" /></label>
      <button :disabled="auth.state.loading" type="submit">
        {{ auth.state.loading ? "Signing in..." : "Login" }}
      </button>
    </form>

    <details class="mt">
      <summary>Create account</summary>
      <form @submit.prevent="onRegister" class="mt">
        <label>Username <input v-model="rUsername" /></label>
        <label>Email <input v-model="rEmail" type="email" /></label>
        <label>Password <input v-model="rPassword" type="password" /></label>
        <button :disabled="auth.state.loading" type="submit">
          {{ auth.state.loading ? "Registering..." : "Register" }}
        </button>
      </form>
    </details>

    <p v-if="auth.state.error" class="error">{{ auth.state.error }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useAuth } from "../composables/useAuth";

const auth = useAuth();

// login form state
const username = ref("");
const password = ref("");

// register form state
const rUsername = ref("");
const rEmail = ref("");
const rPassword = ref("");

// login handler
async function onLogin() {
  await auth.login({
    username: username.value,
    password: password.value
  });

  console.log("document.cookie after login:", document.cookie);
}

// register then auto-login
async function onRegister() {
  // create account
  await auth.register({
    username: rUsername.value,
    email: rEmail.value,
    password: rPassword.value
  });

  // immediately login
  await auth.login({
    username: rUsername.value,
    password: rPassword.value
  });

  // optionally clear register fields
  rUsername.value = "";
  rEmail.value = "";
  rPassword.value = "";
}

// when page mounts, check if session already exists
onMounted(() => auth.refreshMe());
</script>

<style scoped>
.card { max-width: 420px; margin: 1.5rem auto; padding: 1rem; border: 1px solid #ddd; border-radius: 10px; display: grid; gap: .6rem; }
label { display: grid; gap: .25rem; }
input { padding: .5rem; border: 1px solid #ccc; border-radius: 6px; }
button { padding: .5rem .8rem; border-radius: 6px; cursor: pointer; }
.error { color: #a00; }
.mt { margin-top: .6rem; }
</style>
