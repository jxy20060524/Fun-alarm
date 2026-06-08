<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { statsApi } from '../api'
import { useUserStore } from '../stores'

const router = useRouter()
const userStore = useUserStore()
const todayWake = ref(null)
const streak = ref(0)
const recent = ref([])
const error = ref('')

const dayLabels = ['周一','周二','周三','周四','周五','周六','周日']

function formatTime(v) {
  if (!v) return '--:--'
  return String(v).substring(11, 19)
}

function formatAlarmTime(t) {
  return t ? String(t).substring(0, 5) : '--:--'
}

function isEarly(session) {
  if (!session.success || !session.successAt) return false
  const deadline = new Date(`${session.sessionDate}T${session.alarmTime}`)
  deadline.setMinutes(deadline.getMinutes() + 20)
  return new Date(session.successAt) <= deadline
}

onMounted(async () => {
  try {
    const today = await statsApi.today(userStore.user.userId)
    todayWake.value = today.wakeTime
    const s = await statsApi.streak(userStore.user.userId)
    streak.value = s.streakDays
    recent.value = await statsApi.recent(userStore.user.userId)
  } catch (e) {
    error.value = e.message
  }
})
</script>

<template>
  <div class="page">
    <h1 class="title">起床统计</h1>
    <div class="card">
      <p>今日起床时间：{{ formatTime(todayWake) }}</p>
      <p style="color:#2980b9;font-weight:700;margin-top:8px">连续早起天数：{{ streak }} 天</p>
    </div>
    <h3 style="margin:16px 0 8px">最近记录</h3>
    <p v-if="error" class="error">{{ error }}</p>
    <div v-if="!recent.length" class="card">暂无历史记录</div>
    <div v-for="s in recent" :key="s.sessionId" class="card" style="font-size:14px">
      {{ s.sessionDate }} {{ formatAlarmTime(s.alarmTime) }}
      → {{ s.successAt ? formatTime(s.successAt) : '--' }}
      {{ s.success ? '成功' : '未完成' }}
      {{ isEarly(s) ? '（早起）' : '' }}
    </div>
    <button class="btn-secondary btn-block" @click="router.push('/alarms')">返回首页</button>
  </div>
</template>
