<script>
  import { onMount } from 'svelte';
  /**
   * @type {any[] | null | undefined}
   */
  let items = [];

  onMount(async() => {
    console.time("Render 10k Svelte");
    items = Array.from({ length: 10000 }, (_, i) => `Item ${i}`);
    await new Promise(requestAnimationFrame);
    console.timeEnd("Render 10k Svelte");

    const result = performance.now();
  const existing = JSON.parse(localStorage.getItem("svelteResults") || "[]");
  existing.push(result);
  localStorage.setItem("svelteResults", JSON.stringify(existing));
  console.log("Saved result:", result);
  });
</script>

{#each items as item}
  <div>{item}</div>
{/each}

