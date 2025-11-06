<script setup>
import { ref, onMounted } from 'vue';

const items = ref([]);
onMounted(() => {
  console.time("Render 10k Vue");
  items.value = Array.from({ length: 10000 }, (_, i) => `Item ${i}`);
  console.timeEnd("Render 10k Vue");
});
</script>

<template>
  <div>
    <div v-for="item in items" :key="item">{{ item }}</div>
  </div>
</template>
