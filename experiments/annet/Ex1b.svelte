<script>
  import { createEventDispatcher } from "svelte";

  const dispatch = createEventDispatcher();

  let users = [
    { id: 1, name: "Hanna" },
  { id: 2, name: "Johannes" },
  { id: 3, name: "Thone" },
  { id: 4, name: "Magnus" }
  ];

  function selectUser(id) {
    dispatch("select-user", id);
  }

   onMount(() => {
    const end = performance.now();
    console.log("Svelte: Render time (ms):", end - start);
  });
</script>

<div class="border p-4 rounded">
  <h3>Select user</h3>
  <ul>
    {#each users as user (user.id)}
      <li>
        <button on:click={() => selectUser(user.id)}>
          {user.name}
        </button>
      </li>
    {/each}
  </ul>
</div>
