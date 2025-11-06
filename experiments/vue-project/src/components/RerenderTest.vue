<template>
  <div>
    <p>Count: {{ count }}</p>
    <button @click="rerenderTest">Run rerender test ({{ UPDATES }} updates)</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const count = ref(0);
const UPDATES = 1000;

function rerenderTest() {
  console.time("Vue rerender");
  for (let i = 0; i < UPDATES; i++) {
    count.value = i;
  }
  Promise.resolve().then(() => {
    console.timeEnd("Vue rerender");
  });
}
</script>
