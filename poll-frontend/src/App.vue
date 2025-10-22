<script setup lang="ts">
import { ref } from "vue";
import UserPanel from "./components/UserPanel.vue";
import PollList from "./components/PollList.vue";
import PollVote from "./components/PollVote.vue";
import CreatePoll from "./components/CreatePoll.vue";
import type { PollDto } from "./api/polls";

const selectedPoll = ref<PollDto | null>(null);
const refreshFlag = ref(false);
const activeUserId = ref<number | null>(null);

function handleSelect(poll: PollDto) {
  selectedPoll.value = poll;
}

function refreshList() {
  refreshFlag.value = !refreshFlag.value;
}

function onUserSelect(id: number) {
  activeUserId.value = id;
}
</script>

<template>
  <div class="p-4">
    <UserPanel @select-user="onUserSelect" />

    <div v-if="!selectedPoll">
      <CreatePoll v-if="activeUserId" @created="refreshList" />
      <PollList :key="Number(refreshFlag)" @select="handleSelect" />
    </div>

    <PollVote v-else :poll="selectedPoll" />
  </div>
</template>
