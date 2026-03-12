<template>
    <div class="page-wrap">
      <PageNav :links="navLinks" />

      <div class="nd-wrap">
        <main class="nd-main">
          <div class="nd-top">
            <div>
              <div class="nd-author-row">
                <div class="avatar">K</div>
                <div>
                  <div class="nd-author-name">kimwine</div>
                  <div class="nd-date">2026. 03. 11</div>
                </div>
              </div>
            </div>
          </div>

          <div class="nd-drink-pill">🍷 Château Margaux 2018</div>
          <div class="nd-stars">★★★★★</div>
          <div class="nd-photo">🍷</div>
          <div class="nd-text">잔에 따르자마자 올라오는 블랙커런트와 시가박스 향이 인상적이었다. 타닌이 부드럽고 여운이 길어 한 모금씩 천천히 즐기기 좋았다. 지금 마셔도 충분히 좋지만 몇 년 더 숙성해도 기대가 되는 와인이다.</div>
          <div class="nd-like-row">
            <button class="like-btn" @click="liked = !liked">{{ liked ? '♥' : '♡' }} {{ 24 + (liked ? 1 : 0) }}</button>
            <button class="report-btn" @click="reported = !reported">{{ reported ? '신고 완료' : '신고' }}</button>
          </div>
        </main>

        <aside class="nd-side">
          <div class="nd-flavor-title">맛 프로파일</div>
          <div v-for="item in noteDetailFlavor" :key="item.label" class="nd-flavor-row">
            <span class="nfl">{{ item.label }}</span>
            <div class="nfb">
              <div class="nff" :style="{ width: item.width }"></div>
            </div>
            <span class="nfv">{{ item.value }}</span>
          </div>

          <div class="nd-comments">
            <h4>댓글</h4>
            <CommentItem v-for="comment in commentList" :key="comment.id" :item="comment" />
            <form class="comment-inp" @submit.prevent="submitComment">
              <input v-model="newComment" placeholder="댓글을 입력하세요" />
              <button class="cm-send">등록</button>
            </form>
          </div>
        </aside>
      </div>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import PageNav from '../components/common/PageNav.vue'
import CommentItem from '../components/cards/CommentItem.vue'
import { comments, noteDetailFlavor } from '../mock/soolData'

const route = useRoute()
const liked = ref(false)
const reported = ref(false)
const newComment = ref('')
const commentList = ref([...comments])

const navLinks = computed(() => [
  { label: '홈', to: '/' },
  { label: `테이스팅 노트 #${route.params.id || 1}`, to: route.fullPath, active: true }
])

const submitComment = () => {
  const text = newComment.value.trim()
  if (!text) return
  commentList.value.push({
    id: Date.now(),
    authorInitial: '나',
    author: 'me',
    date: '방금',
    text,
    likes: 0,
    replies: []
  })
  newComment.value = ''
}
</script>

<style scoped>
.page-wrap {
  background: var(--white);
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.12);
}

.nd-wrap {
  display: grid;
  grid-template-columns: 1fr 400px;
  min-height: 700px;
}

.nd-main {
  padding: 40px 48px;
  border-right: 1px solid var(--border);
}

.nd-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 24px;
}

.nd-author-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--ink);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
}

.nd-author-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--ink);
}

.nd-date {
  font-size: 12px;
  color: var(--muted);
}

.nd-drink-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: #fdf3ef;
  border: 1px solid #f0c9b9;
  border-radius: 100px;
  font-size: 12.5px;
  color: var(--point);
  font-weight: 500;
  margin-bottom: 16px;
}

.nd-stars {
  font-size: 18px;
  color: var(--yellow);
  margin-bottom: 16px;
}

.nd-photo {
  width: 100%;
  height: 220px;
  background: var(--surface);
  border-radius: 10px;
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60px;
  margin-bottom: 20px;
}

.nd-text {
  font-size: 14.5px;
  color: var(--sub);
  line-height: 1.8;
  font-weight: 300;
  margin-bottom: 24px;
}

.nd-like-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border: 1.5px solid var(--point);
  background: #fdf3ef;
  border-radius: 100px;
  font-size: 13px;
  color: var(--point);
  font-weight: 600;
}

.report-btn {
  font-size: 12px;
  color: var(--muted);
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px 12px;
}

.nd-side {
  background: var(--surface);
  padding: 32px 26px;
}

.nd-flavor-title,
.nd-comments h4 {
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 600;
  margin-bottom: 16px;
}

.nd-flavor-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.nfl {
  font-size: 12.5px;
  color: var(--sub);
  width: 48px;
}

.nfb {
  flex: 1;
  height: 6px;
  background: var(--border);
  border-radius: 3px;
  overflow: hidden;
}

.nff {
  height: 100%;
  background: var(--point2);
  border-radius: 3px;
}

.nfv {
  font-size: 12px;
  font-weight: 700;
  color: var(--ink);
}

.nd-comments {
  margin-top: 28px;
}

.comment-inp {
  margin-top: 14px;
  display: flex;
  gap: 6px;
}

.comment-inp input {
  flex: 1;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 0 12px;
  font-size: 12.5px;
  background: white;
  color: var(--ink);
}

.cm-send {
  height: 36px;
  padding: 0 14px;
  background: var(--point);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}
</style>
