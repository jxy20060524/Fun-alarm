<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useAlarmScheduler } from './composables/useAlarmScheduler'
import { useRingStore } from './stores'
import RingOverlay from './components/RingOverlay.vue'
import AudioUnlockPrompt from './components/AudioUnlockPrompt.vue'

useAlarmScheduler()

const ringStore = useRingStore()

function onVisible() {
  if (document.visibilityState === 'visible' && ringStore.visible) {
    ringStore.retryRingtone()
  }
}

onMounted(() => {
  ringStore.preload()
  document.addEventListener('visibilitychange', onVisible)
})

onUnmounted(() => {
  document.removeEventListener('visibilitychange', onVisible)
})
</script>

<template>
  <router-view />
  <AudioUnlockPrompt />
  <RingOverlay />
</template>
