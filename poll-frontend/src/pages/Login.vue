<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { loginUser, registerUser } from "../api/api.js";

const router = useRouter();

// login fields
const loginUsername = ref("");
const loginPassword = ref("");
const loginError = ref("");

// register fields
const registerUsername = ref("");
const registerEmail = ref("");
const registerPassword = ref("");
const registerError = ref("");

const showRegister = ref(false);

// login flow
const login = async () => {
  loginError.value = "";
  try {
    await loginUser(loginUsername.value, loginPassword.value);
    router.push("/home");
  } catch {
    loginError.value = "Invalid credentials";
  }
};

// register then auto-login
const register = async () => {
  registerError.value = "";
  try {
    await registerUser(
        registerUsername.value,
        registerEmail.value,
        registerPassword.value
    );

    await loginUser(registerUsername.value, registerPassword.value);
    router.push("/home");
  } catch {
    registerError.value = "Registration failed";
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <div class="w-full max-w-sm space-y-6 p-6 border bg-white">

      <!-- login -->
      <form v-if="!showRegister" class="space-y-4" @submit.prevent="login">
        <h1 class="text-lg font-semibold">Sign in</h1>

        <input v-model="loginUsername" placeholder="Username" class="w-full border p-2 focus:outline-none focus:ring" required />
        <input v-model="loginPassword" type="password" placeholder="Password" class="w-full border p-2 focus:outline-none focus:ring" required />

        <button class="w-full py-2 bg-blue-600 text-white">Login</button>
        <div v-if="loginError" class="text-red-600 text-sm">{{ loginError }}</div>

        <button type="button" class="w-full text-sm text-blue-600 underline" @click="showRegister = true">
          Create account
        </button>
      </form>

      <!-- register -->
      <form v-else class="space-y-4" @submit.prevent="register">
        <h1 class="text-lg font-semibold">Create account</h1>

        <input v-model="registerUsername" placeholder="Username" class="w-full border p-2 focus:outline-none focus:ring" required />
        <input v-model="registerEmail" placeholder="Email" class="w-full border p-2 focus:outline-none focus:ring" required />
        <input v-model="registerPassword" type="password" placeholder="Password" class="w-full border p-2 focus:outline-none focus:ring" required />

        <button class="w-full py-2 bg-green-600 text-white">Register</button>
        <div v-if="registerError" class="text-red-600 text-sm">{{ registerError }}</div>

        <button type="button" class="w-full text-sm text-blue-600 underline" @click="showRegister = false">
          Back to login
        </button>
      </form>

    </div>
  </div>
</template>
