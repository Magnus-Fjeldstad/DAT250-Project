<script>
  import ex1b from "./Ex1b.svelte";
  let activeUserId = null;
</script>

<ex1b on:select-user={(e) => activeUserId = e.detail} />
<p>Selected user: {activeUserId}</p>
