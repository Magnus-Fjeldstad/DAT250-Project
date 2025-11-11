<script>
  import StressTest from '$lib/StressTest.svelte';
  import RerenderTest from '$lib/RerenderTest.svelte';
  import Stress2 from '$lib/Stress2.svelte';
</script>

<h1>Svelte: Stress + Rerender tester</h1>

<section>
  <h2> Stress </h2>
  <Stress2 />
</section>
<!-- 
<section>
  <h2>Stress test (10k)</h2>
  <StressTest />
</section>

<section style="margin-top: 2rem;">
  <h2>Rerender test</h2>
  <RerenderTest />
</section> -->
