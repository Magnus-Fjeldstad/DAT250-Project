<script setup>
import { ref, onMounted, nextTick } from "vue";

const items = ref([]);
onMounted(async () => {
  console.time("Render 10k Vue");
  items.value = Array.from({ length: 10000 }, (_, i) => `Item ${i}`);
  await nextTick();
  await new Promise(requestAnimationFrame);
  console.timeEnd("Render 10k Vue");

 
  const existing = JSON.parse(localStorage.getItem("vueResults") || "[]");
  const result = performance.now();
  existing.push(result);
  localStorage.setItem("vueResults", JSON.stringify(existing));
  console.log("Saved result:", result);
});
</script>

<template>
  <div>
    <div v-for="item in items" :key="item">{{ item }}</div>
  </div>
</template>
