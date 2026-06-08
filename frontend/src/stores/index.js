import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const user = ref(JSON.parse(localStorage.getItem('fun_alarm_user') || 'null'))

  const isLoggedIn = computed(() => !!user.value)

  function setUser(u) {
    user.value = u
    localStorage.setItem('fun_alarm_user', JSON.stringify(u))
  }

  function logout() {
    user.value = null
    localStorage.removeItem('fun_alarm_user')
  }

  return { user, isLoggedIn, setUser, logout }
})

export const useAlarmStore = defineStore('alarm', () => {
  const activeAlarms = ref([])
  const triggeredToday = ref(new Set())

  function setActiveAlarms(list) {
    activeAlarms.value = list
  }

  function markTriggered(alarmId) {
    triggeredToday.value.add(alarmId)
  }

  function resetDailyIfNeeded() {
    const today = new Date().toDateString()
    const saved = localStorage.getItem('fun_alarm_trigger_date')
    if (saved !== today) {
      triggeredToday.value = new Set()
      localStorage.setItem('fun_alarm_trigger_date', today)
    }
  }

  return { activeAlarms, triggeredToday, setActiveAlarms, markTriggered, resetDailyIfNeeded }
})

export const useRingStore = defineStore('ring', () => {
  const visible = ref(false)
  const alarm = ref(null)
  const session = ref(null)
  const question = ref(null)
  const message = ref('')
  const audio = ref(null)

  function playRingtone(filename) {
    stopRingtone()
    if (!filename) return
    audio.value = new Audio(`/sounds/${encodeURIComponent(filename)}`)
    audio.value.loop = true
    audio.value.play().catch(() => {})
  }

  function stopRingtone() {
    if (audio.value) {
      audio.value.pause()
      audio.value = null
    }
  }

  function open(payload) {
    alarm.value = payload.alarm
    session.value = payload.session
    question.value = payload.question
    message.value = ''
    visible.value = true
    playRingtone(payload.alarm.ringtone)
  }

  function closeRingtoneOnly() {
    stopRingtone()
  }

  function close() {
    stopRingtone()
    visible.value = false
  }

  return {
    visible, alarm, session, question, message, audio,
    open, close, closeRingtoneOnly, playRingtone, stopRingtone
  }
})
