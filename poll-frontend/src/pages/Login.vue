<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { loginUser, registerUser } from "../api/api.js";

const router = useRouter();

const loginUsername = ref("");
const loginPassword = ref("");
const loginError = ref("");

const registerUsername = ref("");
const registerEmail = ref("");
const registerPassword = ref("");
const registerError = ref("");

const showRegister = ref(false);

const login = async () => {
  loginError.value = "";
  try {
    await loginUser(loginUsername.value, loginPassword.value);
    router.push("/home");
  } catch {
    loginError.value = "Invalid username or password";
  }
};

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
  <div class="min-h-screen flex justify-center items-center bg-gray-100 p-6">
    <div
        class="w-full max-w-sm p-8 rounded-2xl shadow-2xl bg-white/80 backdrop-blur-md border border-gray-200 animate-fadeIn"
    >
      <transition name="fade" mode="out-in">
        <form
            v-if="!showRegister"
            @submit.prevent="login"
            class="space-y-6"
            key="login"
        >
          <h1 class="text-xl font-semibold text-center text-gray-800">
            Welcome back
          </h1>

          <div class="space-y-4">
            <div class="input-group">
              <input v-model="loginUsername" required class="input" placeholder=" " />
              <label>Username</label>
            </div>

            <div class="input-group">
              <input v-model="loginPassword" type="password" required class="input" placeholder=" " />
              <label>Password</label>
            </div>
          </div>

          <button class="btn-primary w-full">Sign in</button>

          <div v-if="loginError" class="text-sm text-red-600 text-center">
            {{ loginError }}
          </div>

          <button
              type="button"
              class="text-sm text-blue-600 hover:text-blue-700 font-medium w-full mt-2"
              @click="showRegister = true"
          >
            Create an account
          </button>
        </form>

        <form
            v-else
            @submit.prevent="register"
            class="space-y-6"
            key="register"
        >
          <h1 class="text-xl font-semibold text-center text-gray-800">
            Create account
          </h1>

          <div class="space-y-4">
            <div class="input-group">
              <input v-model="registerUsername" required class="input" placeholder=" " />
              <label>Username</label>
            </div>

            <div class="input-group">
              <input v-model="registerEmail" required class="input" placeholder=" " />
              <label>Email</label>
            </div>

            <div class="input-group">
              <input v-model="registerPassword" required type="password" class="input" placeholder=" " />
              <label>Password</label>
            </div>
          </div>

          <button class="btn-green w-full">Register</button>

          <div v-if="registerError" class="text-sm text-red-600 text-center">
            {{ registerError }}
          </div>

          <button
              type="button"
              class="text-sm text-blue-600 hover:text-blue-700 font-medium w-full mt-2"
              @click="showRegister = false"
          >
            Back to login
          </button>
        </form>
      </transition>
    </div>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity .25s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fadeIn {
  animation: fadeIn .25s ease-out;
}

.input-group {
  position: relative;
}
.input {
  width: 100%;
  padding: 12px;
  padding-top: 18px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  background: #f9fafb;
  transition: border .2s, background .2s;
}
.input:focus {
  border-color: #2563eb;
  background: white;
  outline: none;
}
.input + label {
  position: absolute;
  top: 12px;
  left: 12px;
  font-size: .85rem;
  color: #6b7280;
  transition: .2s;
  pointer-events: none;
}
.input:focus + label,
.input:not(:placeholder-shown) + label {
  top: -10px;
  left: 10px;
  font-size: .7rem;
  background: white;
  padding: 0 4px;
  color: #2563eb;
}

.btn-primary {
  background: #2563eb;
  color: white;
  padding: 10px;
  font-weight: 600;
  border-radius: 8px;
  transition: background .2s;
}
.btn-primary:hover {
  background: #1e50c4;
}

.btn-green {
  background: #16a34a;
  color: white;
  padding: 10px;
  font-weight: 600;
  border-radius: 8px;
  transition: background .2s;
}
.btn-green:hover {
  background: #11863c;
}
</style>
