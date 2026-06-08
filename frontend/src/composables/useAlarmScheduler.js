import { onMounted, onUnmounted, watch } from 'vue'
import { useUserStore, useAlarmStore, useRingStore } from '../stores'
import { alarmApi, quizApi } from '../api'

let timer = null

export function useAlarmScheduler() {
  const userStore = useUserStore()
  const alarmStore = useAlarmStore()
  const ringStore = useRingStore()

  async function refreshAlarms() {
    if (!userStore.user) return
    const list = await alarmApi.listActive(userStore.user.userId)
    alarmStore.setActiveAlarms(list)
  }

  function isRepeatToday(repeatDays) {
    const day = new Date().getDay()
    const index = day === 0 ? 6 : day - 1
    return repeatDays?.charAt(index) === '1'
  }

  function formatNowHm() {
    const n = new Date()
    return `${String(n.getHours()).padStart(2, '0')}:${String(n.getMinutes()).padStart(2, '0')}`
  }

  async function checkAlarms() {
    if (!userStore.user || ringStore.visible) return
    alarmStore.resetDailyIfNeeded()
    const now = formatNowHm()
    for (const alarm of alarmStore.activeAlarms) {
      if (!alarm.active) continue
      if (!isRepeatToday(alarm.repeatDays)) continue
      const t = String(alarm.alarmTime).substring(0, 5)
      if (t !== now) continue
      if (alarmStore.triggeredToday.has(alarm.alarmId)) continue
      try {
        const data = await quizApi.startSession(alarm.alarmId)
        alarmStore.markTriggered(alarm.alarmId)
        ringStore.open(data)
      } catch (e) {
        if (e.message?.includes('今日已响铃')) {
          alarmStore.markTriggered(alarm.alarmId)
        }
      }
    }
  }

  function start() {
    if (timer) return
    timer = setInterval(checkAlarms, 1000)
  }

  function stop() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  watch(() => userStore.isLoggedIn, async (loggedIn) => {
    if (loggedIn) {
      await refreshAlarms()
      start()
    } else {
      stop()
    }
  }, { immediate: true })

  onUnmounted(stop)

  return { refreshAlarms, start, stop }
}
