<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { alarmApi, userApi } from '../api'
import { useUserStore, useAlarmStore } from '../stores'

const router = useRouter()
const userStore = useUserStore()
const alarmStore = useAlarmStore()
const alarms = ref([])
const error = ref('')

const dayLabels = ['周一','周二','周三','周四','周五','周六','周日']

function formatRepeat(repeatDays) {
  if (!repeatDays || repeatDays.length !== 7) return ''
  return repeatDays.split('').map((c, i) => c === '1' ? dayLabels[i] : '').filter(Boolean).join(' ')
}

function formatTime(t) {
  return t ? t.substring(0, 5) : '--:--'
}

async function refreshActive() {
  const list = await alarmApi.listActive(userStore.user.userId)
  alarmStore.setActiveAlarms(list)
}

async function load() {
  try {
    alarms.value = await alarmApi.list(userStore.user.userId)
    await refreshActive()
  } catch (e) {
    error.value = e.message
  }
}

async function toggle(alarm) {
  await alarmApi.update(alarm.alarmId, {
    userId: alarm.userId,
    alarmTime: alarm.alarmTime,
    repeatDays: alarm.repeatDays,
    ringtone: alarm.ringtone,
    active: !alarm.active,
    label: alarm.label
  })
  await load()
}

async function remove(alarm) {
  if (!confirm('确定删除这个闹钟吗？')) return
  await alarmApi.remove(alarm.alarmId, userStore.user.userId)
  await load()
}

async function editNickname() {
  const nickname = prompt('新昵称', userStore.user.nickname)
  if (!nickname) return
  const user = await userApi.updateNickname(userStore.user.userId, nickname)
  userStore.setUser(user)
}

function logout() {
  userStore.logout()
  router.push('/login')
}

onMounted(load)
</script>

<template>
  <div class="page">
    <h1 class="title">我的闹钟</h1>
    <p class="subtitle">你好，{{ userStore.user.nickname }} · 请保持本页面打开以便到点响铃</p>
    <div class="toolbar">
      <button class="btn-primary" @click="router.push('/alarms/new')">+ 新建闹钟</button>
      <button class="btn-secondary" @click="router.push('/stats')">起床统计</button>
      <button class="btn-secondary" @click="editNickname">修改昵称</button>
      <button class="btn-secondary" @click="logout">退出登录</button>
    </div>
    <p v-if="error" class="error">{{ error }}</p>
    <div v-if="!alarms.length" class="card">还没有闹钟，点击「新建闹钟」添加</div>
    <div v-for="a in alarms" :key="a.alarmId" class="card">
      <div class="time-big">{{ formatTime(a.alarmTime) }}</div>
      <div>{{ a.label || '闹钟' }} · {{ formatRepeat(a.repeatDays) }} · {{ a.active ? '已启用' : '已禁用' }}</div>
      <div class="toolbar" style="margin-top:12px;margin-bottom:0">
        <button class="btn-secondary" @click="router.push(`/alarms/${a.alarmId}/edit`)">编辑</button>
        <button class="btn-secondary" @click="toggle(a)">{{ a.active ? '禁用' : '启用' }}</button>
        <button class="btn-danger" @click="remove(a)">删除</button>
      </div>
    </div>
  </div>
</template>
