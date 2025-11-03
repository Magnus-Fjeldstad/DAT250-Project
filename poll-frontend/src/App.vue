<!-- src/App.vue -->
<template>
  <main>
    <h1>Poll App</h1>
    <UserPanel />

    <section v-if="!auth.state.user">
      <!-- Guest view: can see polls and results but not vote or create -->
      <LoginForm />
      <PollList @edit="onEdit" />
      <div v-if="selectedId" class="mt">
        <PollVote :pollId="selectedId" />
      </div>
    </section>

    <section v-else>
      <!-- Authenticated view -->
      <PollList @edit="onEdit" />
      <CreatePoll class="mt" />

      <div v-if="selectedId" class="mt">
        <PollVote :pollId="selectedId" />
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuth } from "./composables/useAuth";
import LoginForm from "./components/LoginForm.vue";
import UserPanel from "./components/UserPanel.vue";
import PollList from "./components/PollList.vue";
import CreatePoll from "./components/CreatePoll.vue";
import PollVote from "./components/PollVote.vue";

const auth = useAuth();
const selectedId = ref<number | null>(null);

function onEdit(poll: { id?: number }) {
  if (poll.id) selectedId.value = poll.id;
}
</script>

<style>
main { max-width: 900px; margin: 2rem auto; padding: 0 1rem; }
.mt { margin-top: 1rem; }
</style>
