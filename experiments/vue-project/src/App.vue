<template>
  <div>
    <h1>Vue: Stress + Rerender tester</h1>

    <section>
      <h2>Stress test (10k)</h2>
      <StressTest />
    </section>

    <section style="margin-top: 2rem;">
      <h2>Rerender test</h2>
      <RerenderTest />
    </section>
  </div>
</template>

<script setup>
import StressTest from './components/stresstest.vue';
import RerenderTest from './components/RerenderTest.vue';
</script>
