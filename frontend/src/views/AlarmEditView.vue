<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { alarmApi, metaApi } from '../api'
import { useUserStore, useAlarmStore } from '../stores'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const alarmStore = useAlarmStore()

const isNew = computed(() => route.path.endsWith('/new'))
const alarmId = computed(() => isNew.value ? null : Number(route.params.id))

const form = ref({
  alarmTime: '07:00',
  repeatDays: '1111100',
  ringtone: '',
  active: true,
  label: '起床'
})
const ringtones = ref([])
const days = ['周一','周二','周三','周四','周五','周六','周日']
const dayChecks = ref([true,true,true,true,true,false,false])
const error = ref('')

function buildRepeatDays() {
  return dayChecks.value.map(c => c ? '1' : '0').join('')
}

async function load() {
  ringtones.value = await metaApi.ringtones()
  if (!form.value.ringtone && ringtones.value.length) {
    form.value.ringtone = ringtones.value[0]
  }
  if (!isNew.value) {
    const list = await alarmApi.list(userStore.user.userId)
    const alarm = list.find(a => a.alarmId === alarmId.value)
    if (!alarm) throw new Error('闹钟不存在')
    form.value = {
      alarmTime: alarm.alarmTime.substring(0, 5),
      repeatDays: alarm.repeatDays,
      ringtone: alarm.ringtone,
      active: alarm.active,
      label: alarm.label || ''
    }
    dayChecks.value = alarm.repeatDays.split('').map(c => c === '1')
  }
}

async function save() {
  error.value = ''
  const payload = {
    userId: userStore.user.userId,
    alarmTime: form.value.alarmTime + ':00',
    repeatDays: buildRepeatDays(),
    ringtone: form.value.ringtone,
    active: form.value.active,
    label: form.value.label
  }
  try {
    if (isNew.value) await alarmApi.create(payload)
    else await alarmApi.update(alarmId.value, payload)
    const list = await alarmApi.listActive(userStore.user.userId)
    alarmStore.setActiveAlarms(list)
    router.push('/alarms')
  } catch (e) {
    error.value = e.message
  }
}

onMounted(load)
</script>

<template>
  <div class="page">
    <h1 class="title">{{ isNew ? '新建闹钟' : '编辑闹钟' }}</h1>
    <div class="card">
      <label>闹钟时间</label>
      <input v-model="form.alarmTime" type="time" />
      <label style="display:block;margin-top:12px">重复</label>
      <div style="display:grid;grid-template-columns:repeat(2,1fr);gap:8px;margin:8px 0">
        <label v-for="(d,i) in days" :key="d" style="display:flex;gap:6px;align-items:center">
          <input type="checkbox" v-model="dayChecks[i]" /> {{ d }}
        </label>
      </div>
      <label>铃声</label>
      <select v-model="form.ringtone">
        <option v-for="r in ringtones" :key="r" :value="r">{{ r }}</option>
      </select>
      <label style="display:block;margin-top:12px">备注</label>
      <input v-model="form.label" placeholder="如：起床" />
      <label style="display:flex;gap:8px;align-items:center;margin-top:12px">
        <input type="checkbox" v-model="form.active" /> 启用闹钟
      </label>
      <p v-if="error" class="error">{{ error }}</p>
      <div class="toolbar" style="margin-top:16px">
        <button class="btn-primary" @click="save">保存</button>
        <button class="btn-secondary" @click="router.push('/alarms')">取消</button>
      </div>
    </div>
  </div>
</template>
