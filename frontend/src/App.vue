<script setup>
import { useAlarmScheduler } from './composables/useAlarmScheduler'
import RingOverlay from './components/RingOverlay.vue'

useAlarmScheduler()
</script>

<template>
  <router-view />
  <RingOverlay />
</template>
