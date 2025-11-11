<script>
  import { onMount } from 'svelte';
  const framework = "Svelte";
  /**
   * @type {any[] | null | undefined}
   */
  let items = [];

  onMount(async () => {
    // Page load timing
    const startLoad = performance.now();
    await new Promise(requestAnimationFrame);
    const pageLoadTime = performance.now() - startLoad;
    saveResult(framework, "page_load", pageLoadTime);

    // Render 10k items timing
    const startRender = performance.now();
    items = Array.from({ length: 10000 }, (_, i) => `Item ${i}`);
    await new Promise(requestAnimationFrame);
    const renderTime = performance.now() - startRender;
    saveResult(framework, "render_10000", renderTime);
  });

  /**
   * @param {string} framework
   * @param {string} type
   * @param {number} time
   */
  function saveResult(framework, type, time) {
    const existing = JSON.parse(localStorage.getItem("results") || "[]");
    existing.push({ framework, type, time });
    localStorage.setItem("results", JSON.stringify(existing));
  }
</script>

{#each items as item}
  <div>{item}</div>
{/each}
