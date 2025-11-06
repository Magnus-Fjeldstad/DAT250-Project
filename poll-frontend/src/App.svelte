<script>
  import Ex1b from "./Ex1b.svelte";
  let activeUserId = null;
</script>

<h2>Svelte User Selection</h2>
<Ex1b on:select-user={(e) => activeUserId = e.detail} />
<p>Selected user: {activeUserId}</p>
