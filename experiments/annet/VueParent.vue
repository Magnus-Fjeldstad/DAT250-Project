<template>
  <UserPanel @select-user="id => activeUserId = id" />
  <p>Selected user: {{ activeUserId }}</p>
</template>

<script setup lang="ts">
import { ref } from "vue";
import ex1a from "./ex1a.vue";

const activeUserId = ref<number | null>(null);
</script>
