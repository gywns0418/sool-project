<template>
    <div class="page-wrap">
      <PageNav :links="navLinks" show-search />
        <div class="resetpassword-page">
            비밀번호 재설정 페이지
        </div>
    </div>
</template>

<script setup>
import PageNav from "@/components/common/PageNav.vue";

const navLinks = [
  { label: '홈', to: '/'}
]
</script>

<style>
</style>