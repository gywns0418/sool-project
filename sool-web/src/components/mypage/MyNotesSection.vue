<template>
  <div>
    <h2 class="my-main-title">내 테이스팅 노트</h2>
    <div class="my-notes-grid">
      <MyNoteCard
        v-for="note in noteList"
        :key="note.id"
        :item="note"
        @edit="$emit('edit', note)"
        @delete="$emit('delete', note.id)"
      />
    </div>
  </div>
</template>

<script setup>
import MyNoteCard from '../cards/MyNoteCard.vue'

defineProps({
  noteList: Array
})

defineEmits(['edit', 'delete'])
</script>

<style scoped>
.my-main-title {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 400;
  color: var(--ink);
  margin-bottom: 20px;
}

.my-notes-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
</style>