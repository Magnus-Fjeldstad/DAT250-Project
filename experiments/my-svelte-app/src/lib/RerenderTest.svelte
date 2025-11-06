<script>
  import { onMount } from 'svelte';
  let count = 0;

  const UPDATES = 1000;

  function rerenderTest() {
    console.time("Svelte rerender");
    for (let i = 0; i < UPDATES; i++) {
      count = i;
    }
    console.timeEnd("Svelte rerender");
  }
</script>

<div>
  <p>Count: {count}</p>
  <button on:click={rerenderTest}>Run rerender test ({UPDATES} updates)</button>
</div>
