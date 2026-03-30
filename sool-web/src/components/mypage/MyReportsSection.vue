<template>
  <div>
    <h2 class="my-main-title">나의 신고 내역</h2>

    <section class="report-block">
      <div class="report-head">
        <h3 class="report-title">신고 목록</h3>
        <span class="report-count">{{ reportList.length }}</span>
      </div>

      <div v-if="reportList.length > 0" class="report-list">
        <article
          v-for="report in reportList"
          :key="report.reportId"
          class="report-card"
        >
          <div class="report-top">
            <div class="report-badges">
              <span class="badge type-badge">
                {{ formatObjType(report.objType) }}
              </span>
              <span
                class="badge status-badge"
                :class="getStatusClass(report.reportStatus)"
              >
                {{ report.reportStatus }}
              </span>
            </div>

            <p class="report-date">
              {{ formatDate(report.createdAt) }}
            </p>
          </div>

          <div class="report-body">
            <p class="report-target"> 
              <span v-if="report.objType === 'NOTE'">
                  테이스팅 노트 : {{ report.noteTitle }}
              </span>
              <span v-else>
                  댓글 : {{ report.commentContent }}
              </span>
            </p>

            <p class="report-reason">
              {{ report.reportReason  || '신고 사유 없음' }}
            </p>
          </div>
        </article>
      </div>

      <p v-else class="empty-text">신고한 내역이 없습니다.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyReports } from '@/api/mypageApi'

const reportList = ref([])

const fetchMyReports = async () => {
  try {
    const res = await getMyReports()
    console.log(res.data)
    reportList.value = res.data || []
  } catch (error) {
    console.log('신고 목록 조회 실패', error)
    reportList.value = []
  }
}

onMounted(() => {
  fetchMyReports()
})

function formatObjType(objType) {
  if (objType === 'NOTE') return '노트'
  if (objType === 'COMMENT') return '댓글'
}

function getStatusClass(statusCode) {
  if (statusCode === 'REPORTED') return 'is-reported'
  if (statusCode === 'PENDING') return 'is-pending'
  if (statusCode === 'DONE') return 'is-done'
  if (statusCode === 'REJECTED') return 'is-rejected'
  return ''
}

function formatDate(value) {
  if (!value) return ''

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')

  return `${year}.${month}.${day}`
}
</script>

<style scoped>
.my-main-title {
  font-family: 'Playfair Display', serif;
  font-size: 20px;
  font-weight: 400;
  color: var(--ink);
  margin-bottom: 20px;
}

.report-block {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.report-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.report-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink);
  margin: 0;
}

.report-count {
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

.report-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.report-card {
  padding: 16px;
  border: 1px solid #ece3da;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

.report-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 12px;
}

.report-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.badge {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
}

.type-badge {
  background: #f8f4ef;
  color: #7a5c45;
}

.status-badge {
  background: #f3eee8;
  color: #7a5c45;
}

.status-badge.is-reported {
  background: #fdf3d6;
  color: #9a6b00;
}

.status-badge.is-pending {
  background: #eaf2ff;
  color: #345ea8;
}

.status-badge.is-done {
  background: #e9f7ee;
  color: #2d7a46;
}

.status-badge.is-rejected {
  background: #fdeaea;
  color: #b44444;
}

.report-date {
  margin: 0;
  font-size: 12px;
  color: #a59a8f;
  white-space: nowrap;
}

.report-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.report-target {
  margin: 0;
  font-size: 14px;
  color: var(--ink);
}

.report-reason {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #6f6257;
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
</style>