import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { createRingtonePlayer } from '../utils/ringtoneAudio'

const ringtonePlayer = createRingtonePlayer()

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
  const audioBlocked = ref(false)
  const audioUnlocked = ref(false)
  const unlockError = ref('')
  const unlocking = ref(false)

  function syncUnlockedState() {
    audioUnlocked.value = ringtonePlayer.isUnlocked()
  }

  function preload() {
    ringtonePlayer.preloadRingtone()
  }

  function unlockFromUserGesture(options = {}) {
    unlocking.value = true
    unlockError.value = ''
    return ringtonePlayer.unlockFromUserGesture(options)
      .then(() => {
        audioUnlocked.value = true
        audioBlocked.value = false
        unlockError.value = ''
        return true
      })
      .catch((err) => {
        audioUnlocked.value = false
        audioBlocked.value = true
        unlockError.value = err?.message || '启用失败，请再试一次'
        return false
      })
      .finally(() => {
        unlocking.value = false
      })
  }

  async function playRingtone(filename) {
    try {
      await ringtonePlayer.playRingtone(filename)
      audioBlocked.value = false
      return true
    } catch {
      audioBlocked.value = true
      return false
    }
  }

  async function retryRingtone() {
    try {
      await ringtonePlayer.retryRingtone(alarm.value?.ringtone)
      audioBlocked.value = false
      audioUnlocked.value = true
      return true
    } catch (err) {
      audioBlocked.value = true
      unlockError.value = err?.message || '播放失败'
      return false
    }
  }

  async function open(payload, { skipPlay = false } = {}) {
    alarm.value = payload.alarm
    session.value = payload.session
    question.value = payload.question
    message.value = ''
    visible.value = true
    if (!skipPlay && !ringtonePlayer.isRinging()) {
      await playRingtone(payload.alarm?.ringtone)
    }
  }

  function stopAlarmSound() {
    ringtonePlayer.stopAlarmSound()
    audioBlocked.value = false
  }

  function close() {
    visible.value = false
    message.value = ''
  }

  function resetAudioState() {
    ringtonePlayer.reset()
    audioUnlocked.value = false
    audioBlocked.value = false
    unlockError.value = ''
    unlocking.value = false
  }

  return {
    visible, alarm, session, question, message, audioBlocked, audioUnlocked,
    unlockError, unlocking,
    preload, open, close, stopAlarmSound, playRingtone, retryRingtone,
    unlockFromUserGesture, resetAudioState, syncUnlockedState
  }
})
