<script setup>
import { ref, onMounted, nextTick } from "vue";

const framework = "Vue";
const items = ref([]);

onMounted(async () => {
  const startLoad = performance.now();
  await nextTick();
  await new Promise(requestAnimationFrame);
  const pageLoadTime = performance.now() - startLoad;
  saveResult(framework, "page_load", pageLoadTime);

  const startRender = performance.now();
  items.value = Array.from({ length: 10000 }, (_, i) => `Item ${i}`);
  await nextTick();
  await new Promise(requestAnimationFrame);
  const renderTime = performance.now() - startRender;
  saveResult(framework, "render_10000", renderTime);
});

function saveResult(framework, type, time) {
  const existing = JSON.parse(localStorage.getItem("results") || "[]");
  existing.push({ framework, type, time });
  localStorage.setItem("results", JSON.stringify(existing));
}
</script>

<template>
  <div>
    <div v-for="item in items" :key="item">{{ item }}</div>
  </div>
</template>
