<template>
  <div>
    <h2 class="my-main-title">내 테이스팅 노트</h2>
    <div class="my-notes-grid">
      <MyNoteCard
        v-for="note in noteList"
        :key="note.noteId"
        :item="note"
        @edit="$emit('edit', note.noteId)"
        @delete="$emit('delete', note.noteId)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MyNoteCard from './card/MyNoteCard.vue'
import { getMyTastingNote } from '@/api/mypageApi'

defineEmits(['edit', 'delete'])

const noteList = ref([])

const fetchMyTastingNote = async () => {
  try{
    const res = await getMyTastingNote()
    console.log(res.data)

    noteList.value = res.data?.noteList || []
  } catch (error) {
    console.log('테이스팅 노트 목록 조회 실패', error)
    noteList.value = []
  }
}

onMounted(() => {
  fetchMyTastingNote()
})
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