<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRingStore } from '../stores'

const route = useRoute()
const router = useRouter()
const ringStore = useRingStore()
const closed = ref(false)

const message = ref(route.query.msg || '今天又是美好的一天！')
const streak = ref(Number(route.query.streak || 0))

function closeAlarm() {
  ringStore.stopRingtone()
  closed.value = true
}

function goHome() {
  if (!closed.value) return
  router.push('/alarms')
}
</script>

<template>
  <div class="page motivation">
    <h1 class="title" style="text-align:center">{{ message }}</h1>
    <p style="text-align:center;color:#27ae60;font-size:22px;font-weight:700;margin:20px 0">
      连续早起 {{ streak }} 天
    </p>
    <button class="btn-danger btn-block" :disabled="closed" @click="closeAlarm">
      {{ closed ? '闹钟已关闭' : '关闭闹钟' }}
    </button>
    <button class="btn-secondary btn-block" :disabled="!closed" @click="goHome">返回首页</button>
  </div>
</template>

<style scoped>
.motivation { background: #eafaf1; min-height: 100vh; padding-top: 60px; }
</style>
