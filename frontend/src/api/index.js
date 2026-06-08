import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 15000
})

api.interceptors.response.use(
  res => res.data,
  err => {
    const message = err.response?.data?.message || err.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

export default api

export const authApi = {
  register: data => api.post('/auth/register', data),
  login: data => api.post('/auth/login', data)
}

export const userApi = {
  updateNickname: (userId, nickname) => api.put(`/users/${userId}/nickname`, { nickname })
}

export const alarmApi = {
  list: userId => api.get('/alarms', { params: { userId } }),
  listActive: userId => api.get('/alarms/active', { params: { userId } }),
  create: data => api.post('/alarms', data),
  update: (id, data) => api.put(`/alarms/${id}`, data),
  remove: (id, userId) => api.delete(`/alarms/${id}`, { params: { userId } })
}

export const quizApi = {
  startSession: alarmId => api.post('/quiz/sessions', { alarmId }),
  submit: data => api.post('/quiz/submit', data)
}

export const statsApi = {
  today: userId => api.get('/stats/today', { params: { userId } }),
  streak: userId => api.get('/stats/streak', { params: { userId } }),
  recent: userId => api.get('/stats/recent', { params: { userId, limit: 10 } })
}

export const metaApi = {
  ringtones: () => api.get('/meta/ringtones')
}
