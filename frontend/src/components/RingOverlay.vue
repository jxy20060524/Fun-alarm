<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { quizApi } from '../api'
import { useRingStore } from '../stores'

const router = useRouter()
const ringStore = useRingStore()
const selected = ref('')

const options = () => [
  { key: 'A', text: ringStore.question?.optionA },
  { key: 'B', text: ringStore.question?.optionB },
  { key: 'C', text: ringStore.question?.optionC },
  { key: 'D', text: ringStore.question?.optionD }
]

async function submit() {
  if (!selected.value) {
    ringStore.message = '请选择一个选项'
    return
  }
  try {
    const result = await quizApi.submit({
      sessionId: ringStore.session.sessionId,
      questionId: ringStore.question.questionId,
      userAnswer: selected.value
    })
    if (result.correct) {
      ringStore.closeRingtoneOnly()
      router.push({
        path: '/motivation',
        query: {
          msg: result.motivation,
          streak: result.streakDays
        }
      })
      ringStore.close()
      return
    }
    if (result.switchedQuestion) {
      ringStore.session = result.session
      ringStore.question = result.nextQuestion
      ringStore.message = result.message
      selected.value = ''
      return
    }
    ringStore.session = result.session
    ringStore.message = result.message
  } catch (e) {
    ringStore.message = e.message
  }
}

function formatTime(t) {
  return t ? String(t).substring(0, 5) : '--:--'
}
</script>

<template>
  <div v-if="ringStore.visible" class="ring-overlay">
    <div class="ring-box">
      <h2 class="ring-title">闹钟响了！答对题目才能关闭</h2>
      <p>闹钟时间：{{ formatTime(ringStore.alarm?.alarmTime) }}</p>
      <h3 class="question">{{ ringStore.question?.content }}</h3>
      <div class="options-grid">
        <button
          v-for="opt in options()"
          :key="opt.key"
          class="option-btn"
          :class="{ selected: selected === opt.key }"
          @click="selected = opt.key"
        >
          {{ opt.key }}. {{ opt.text }}
        </button>
      </div>
      <p v-if="ringStore.message" class="msg">{{ ringStore.message }}</p>
      <button class="btn-primary submit-btn" @click="submit">提交答案</button>
    </div>
  </div>
</template>

<style scoped>
.ring-overlay {
  position: fixed; inset: 0; z-index: 9999;
  background: #fff5f5;
  display: flex; align-items: center; justify-content: center;
  padding: 20px;
}
.ring-box { max-width: 720px; width: 100%; text-align: center; }
.ring-title { color: #e74c3c; font-size: 24px; margin-bottom: 12px; }
.question { font-size: 20px; margin: 20px 0; line-height: 1.5; }
.options-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(140px, 200px));
  gap: 16px;
  justify-content: center;
  margin: 20px auto;
}
.option-btn {
  width: 200px; height: 200px;
  background: #fff; border: 2px solid #bdc3c7; border-radius: 12px;
  padding: 16px; font-size: 16px; font-weight: 600;
  display: flex; align-items: center; justify-content: center;
  text-align: center; word-break: break-word;
}
.option-btn.selected {
  background: #3498db; color: #fff; border-color: #2980b9;
}
.msg { color: #d35400; margin: 12px 0; }
.submit-btn { width: 200px; height: 48px; font-size: 16px; font-weight: 700; }
@media (max-width: 480px) {
  .options-grid { grid-template-columns: 1fr; }
  .option-btn { width: 100%; height: 120px; }
}
</style>
