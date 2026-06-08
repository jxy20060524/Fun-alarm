<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { useUserStore } from '../stores'

const router = useRouter()
const userStore = useUserStore()
const username = ref('')
const nickname = ref('')
const error = ref('')
const info = ref('')

async function login() {
  error.value = ''
  try {
    const user = await authApi.login({ username: username.value })
    userStore.setUser(user)
    router.push('/alarms')
  } catch (e) {
    error.value = e.message
  }
}

async function register() {
  error.value = ''
  info.value = ''
  try {
    const user = await authApi.register({ username: username.value, nickname: nickname.value })
    info.value = '注册成功，请登录'
    username.value = user.username
  } catch (e) {
    error.value = e.message
  }
}
</script>

<template>
  <div class="page">
    <h1 class="title">趣味闹钟</h1>
    <p class="subtitle">答对题目才能关闭闹钟 · 手机电脑均可使用</p>
    <div class="card">
      <label>用户名</label>
      <input v-model="username" placeholder="用户名" />
      <label style="display:block;margin-top:12px">昵称（注册时使用）</label>
      <input v-model="nickname" placeholder="昵称" />
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="info" style="color:#27ae60;margin-top:8px">{{ info }}</p>
      <button class="btn-primary btn-block" @click="login">登录</button>
      <button class="btn-secondary btn-block" @click="register">注册</button>
    </div>
  </div>
</template>
