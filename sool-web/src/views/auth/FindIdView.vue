<template>
    <div class="page-wrap">
      <PageNav :links="navLinks" show-search />
        <div class="findid-page">
            아이디 찾기 페이지
        </div>
    </div>
</template>

<script setup>
import PageNav from '@/components/common/PageNav.vue'

const navLinks = [
  { label: '홈', to: '/'}
]
</script>

<style>
</style>