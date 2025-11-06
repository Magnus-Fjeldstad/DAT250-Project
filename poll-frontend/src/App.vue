<script setup lang="ts">
import { ref } from "vue";
import Ex1a from "./Ex1a.vue";

const activeUserId = ref<number | null>(null);
</script>

<template>
  <div class="p-4">
    <h2>Vue User Selection</h2>
    <UserPanel @select-user="id => activeUserId = id" />
    <p>Selected user: {{ activeUserId }}</p>
  </div>
</template>

