<template>
  <div>
    <h2 class="my-main-title">내 테이스팅 노트</h2>
    <div class="my-notes-grid">
      <MyNoteCard
        v-for="note in noteList"
        :key="note.noteId"
        :item="note"
        @edit="handleEditNote"
        @delete="handleDeleteNote"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MyNoteCard from './card/MyNoteCard.vue'
import { getMyTastingNote, deleteMyTastingNote } from '@/api/mypageApi'

const emit = defineEmits(['refreshSidebar'])

const router = useRouter()

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

async function handleEditNote(noteId) {
  if (!confirm('테이스팅 노트를 수정하시겠습니까?')) return
  
  try {
    router.push(`/notes/${noteId}/edit`)
  } catch (error) {
    console.log('테이스팅 노트 수정 실패', error)
  }
}

async function handleDeleteNote(noteId) {
  if (!confirm('테이스팅 노트를 삭제하시겠습니까?')) return
  
  try {
    await deleteMyTastingNote(noteId)
    alert('삭제되었습니다.')

    noteList.value = noteList.value.filter(
      note => note.noteId !== noteId
    )

    emit('refreshSidebar')
  } catch (error) {
    console.log('테이스팅 노트 삭제 실패', error)
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