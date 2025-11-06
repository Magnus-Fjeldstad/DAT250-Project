<script>
  import { onMount } from 'svelte';
  /**
   * @type {any[] | null | undefined}
   */
  let items = [];

  onMount(() => {
    console.time("Render 10k Svelte");
    items = Array.from({ length: 10000 }, (_, i) => `Item ${i}`);
    console.timeEnd("Render 10k Svelte");
  });
</script>

{#each items as item}
  <div>{item}</div>
{/each}

