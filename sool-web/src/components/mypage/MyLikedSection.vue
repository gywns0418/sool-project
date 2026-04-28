<template>
  <div>
    <h2 class="my-main-title">나의 좋아요</h2>

    <section class="liked-block">
      <div class="liked-head">
        <h3 class="liked-title">좋아요한 노트</h3>
        <span class="liked-count">{{ likedNoteList.length }}</span>
      </div>

      <div v-if="likedNoteList.length > 0" class="liked-grid">
        <MyLikedNoteCard
          v-for="note in likedNoteList"
          :key="note.noteId"
          :item="note"
          @unlike="handleUnlikeNote"
        />
      </div>

      <p v-else class="empty-text">좋아요한 노트가 없습니다.</p>
    </section>

    <section class="liked-block">
      <div class="liked-head">
        <h3 class="liked-title">좋아요한 주류</h3>
        <span class="liked-count">{{ likedDrinkList.length }}</span>
      </div>

      <div v-if="likedDrinkList.length > 0" class="liked-grid">
        <MyLikedDrinkCard
          v-for="drink in likedDrinkList"
          :key="drink.drinkId"
          :item="drink"
          @unlike="handleUnlikeDrink"
        />
      </div>

      <p v-else class="empty-text">좋아요한 주류가 없습니다.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyLikes, deleteMyLike } from '@/api/mypageApi'

import MyLikedNoteCard from './card/MyLikedNoteCard.vue'
import MyLikedDrinkCard from './card/MyLikedDrinkCard.vue'

const emit = defineEmits(['refreshSidebar', 'authError'])

const likedNoteList = ref([])
const likedDrinkList = ref([])

const handleAuthError = (error) => {
  const status = error?.response?.status

  if (status === 401 || status === 403) {
    emit('authError', error)
    return true
  }

  return false
}

const fetchMyLikes = async () => {
  try {
    const res = await getMyLikes()
    console.log(res.data)

    likedNoteList.value = res.data?.likedNoteList || []
    likedDrinkList.value = res.data?.likedDrinkList || []
  } catch (error) {
    const handled = handleAuthError(error)
    if (handled) return

    console.log('좋아요 목록 조회 실패', error)
    likedNoteList.value = []
    likedDrinkList.value = []
  }
}

async function handleUnlikeDrink(drinkId) {
  if (!confirm('좋아요를 취소하시겠습니까?')) return

  try {
    await deleteMyLike({
      objType: 'DRINK',
      objId: drinkId
    })

    likedDrinkList.value = likedDrinkList.value.filter(
      drink => drink.drinkId !== drinkId
    )

    emit('refreshSidebar')
  } catch (error) {
    const handled = handleAuthError(error)
    if (handled) return

    console.log('좋아요 취소 실패', error)
  }
}

async function handleUnlikeNote(noteId) {
  if (!confirm('좋아요를 취소하시겠습니까?')) return
  
  try {
    await deleteMyLike({
      objType: 'NOTE',
      objId: noteId
    })

    likedNoteList.value = likedNoteList.value.filter(
      note => note.noteId !== noteId
    )

    emit('refreshSidebar')
  } catch (error) {
    const handled = handleAuthError(error)
    if (handled) return

    console.log('좋아요 취소 실패', error)
  }
}

onMounted(() => {
  fetchMyLikes()
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

.liked-block + .liked-block {
  margin-top: 32px;
}

.liked-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.liked-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
  margin: 0;
}

.liked-count {
  min-width: 28px;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f3eee8;
  color: #7a5c45;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.liked-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.empty-text {
  padding: 24px 16px;
  border: 1px dashed #d8cfc6;
  border-radius: 14px;
  text-align: center;
  color: #9a8f84;
  font-size: 14px;
  background: #fcfaf7;
}

@media (max-width: 768px) {
  .liked-grid {
    grid-template-columns: 1fr;
  }
}
</style>