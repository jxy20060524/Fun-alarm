<script setup>
import { computed } from 'vue'
import { useUserStore, useRingStore } from '../stores'

const userStore = useUserStore()
const ringStore = useRingStore()

const show = computed(() => userStore.isLoggedIn && !ringStore.audioUnlocked)

function enable(event) {
  event?.stopPropagation?.()
  ringStore.unlockFromUserGesture({ audibilityTest: true })
}
</script>

<template>
  <div v-if="show" class="unlock-bar" @click="enable">
    <p>浏览器需要您授权后才能自动播放闹钟铃声（每个标签页只需一次）</p>
    <p v-if="ringStore.unlockError" class="unlock-error">{{ ringStore.unlockError }}</p>
    <button
      type="button"
      class="btn-primary unlock-btn"
      :disabled="ringStore.unlocking"
      @click.stop="enable"
    >
      {{ ringStore.unlocking ? '启用中...' : '启用铃声' }}
    </button>
    <p class="unlock-tip">点击后应能听到约 1 秒试播，即表示授权成功</p>
  </div>
</template>

<style scoped>
.unlock-bar {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: 16px;
  z-index: 20000;
  background: #fff3cd;
  border: 2px solid #f39c12;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.18);
  text-align: center;
  pointer-events: auto;
  cursor: pointer;
}
.unlock-bar p {
  margin: 0 0 12px;
  color: #7f5a00;
  font-weight: 600;
}
.unlock-error {
  color: #c0392b !important;
}
.unlock-tip {
  margin: 12px 0 0 !important;
  font-size: 13px;
  font-weight: 500 !important;
  color: #9a7b0a !important;
}
.unlock-btn {
  min-width: 160px;
  min-height: 44px;
  pointer-events: auto;
}
.unlock-btn:disabled {
  opacity: 0.7;
  cursor: wait;
}
</style>
