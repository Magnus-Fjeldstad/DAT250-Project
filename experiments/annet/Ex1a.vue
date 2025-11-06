<script setup lang="ts">
import { ref } from "vue";

const users = ref([
  { id: 1, name: "Hanna" },
  { id: 2, name: "Johannes" },
  { id: 3, name: "Thone" },
  { id: 4, name: "Magnus" },
]);

const emit = defineEmits<{
  (e: "select-user", id: number): void;
}>();

function selectUser(id: number) {
  emit("select-user", id);
}

onMounted(() => {
  const end = performance.now();
  console.log("Vue: Render time (ms):", end - start);
});
</script>

<template>
  <div class="border p-4 rounded">
    <h3>Select user</h3>
    <ul>
      <li v-for="user in users" :key="user.id">
        <button @click="selectUser(user.id)">
          {{ user.name }}
        </button>
      </li>
    </ul>
  </div>
</template>
