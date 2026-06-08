const KNOWN_RINGTONES = ['happy.mp3', 'feeling.mp3']

const LEGACY_RINGTONES = {
  'default.wav': 'happy.mp3',
  'Happy-Pharrell Williams#1B92b.mp3': 'happy.mp3',
  'Can.t Stop The Feeling.-Justin Timberlake#gDoWB.mp3': 'feeling.mp3'
}

const KEEPALIVE_VOLUME = 0.001
const RING_VOLUME = 1

const SILENT_WAV =
  'data:audio/wav;base64,UklGRigAAABXQVZFZm10IBIAAAABAAEARKwAAIhYAQACABAAAABkYXRhAgAAAAEA'

export function normalizeRingtone(filename) {
  if (!filename) return KNOWN_RINGTONES[0]
  if (LEGACY_RINGTONES[filename]) return LEGACY_RINGTONES[filename]
  return KNOWN_RINGTONES.includes(filename) ? filename : KNOWN_RINGTONES[0]
}

export function ringtoneUrl(filename) {
  const path = `/sounds/${normalizeRingtone(filename)}`
  return new URL(path, window.location.origin).href
}

export function createRingtonePlayer() {
  let player = null
  let audioCtx = null
  let keepaliveActive = false
  let ringing = false

  function getPlayer() {
    if (!player) {
      player = new Audio()
      player.loop = true
      player.preload = 'auto'
    }
    return player
  }

  function preloadRingtone(filename = KNOWN_RINGTONES[0]) {
    const el = getPlayer()
    const url = ringtoneUrl(filename)
    if (el.src !== url) {
      el.src = url
      el.load()
    }
  }

  function resumeAudioContext() {
    try {
      audioCtx = audioCtx || new (window.AudioContext || window.webkitAudioContext)()
      if (audioCtx.state === 'suspended') {
        audioCtx.resume()
      }
    } catch {
      // ignore
    }
  }

  function waitForCanPlay(el, timeoutMs = 15000) {
    if (el.readyState >= HTMLMediaElement.HAVE_FUTURE_DATA) {
      return Promise.resolve()
    }

    return new Promise((resolve, reject) => {
      const timer = window.setTimeout(() => {
        cleanup()
        reject(new Error('铃声加载超时'))
      }, timeoutMs)

      const onReady = () => {
        cleanup()
        resolve()
      }
      const onError = () => {
        cleanup()
        const code = el.error?.code
        const detail = code === 4 ? '格式不支持' : `错误码 ${code ?? 'unknown'}`
        reject(new Error(`铃声文件加载失败（${detail}）`))
      }
      const cleanup = () => {
        window.clearTimeout(timer)
        el.removeEventListener('loadeddata', onReady)
        el.removeEventListener('canplay', onReady)
        el.removeEventListener('error', onError)
      }

      el.addEventListener('loadeddata', onReady, { once: true })
      el.addEventListener('canplay', onReady, { once: true })
      el.addEventListener('error', onError, { once: true })
      if (!el.src) {
        cleanup()
        reject(new Error('铃声地址为空'))
        return
      }
      el.load()
    })
  }

  async function loadRingtone(el, filename, { audibilityTest = false } = {}) {
    const candidates = [normalizeRingtone(filename), ...KNOWN_RINGTONES.filter((name) => name !== normalizeRingtone(filename))]
    let lastError = null

    for (const name of candidates) {
      try {
        el.src = ringtoneUrl(name)
        el.loop = true
        el.muted = false
        await waitForCanPlay(el)
        el.volume = audibilityTest ? 0.35 : KEEPALIVE_VOLUME
        await el.play()
        return name
      } catch (err) {
        lastError = err
      }
    }

    throw lastError || new Error('铃声文件加载失败')
  }

  function unlockFromUserGesture({ audibilityTest = false } = {}) {
    resumeAudioContext()
    const el = getPlayer()

    el.src = SILENT_WAV
    el.loop = false
    el.volume = 0.001
    el.muted = false

    const gesturePlay = el.play()
    if (!gesturePlay) {
      return Promise.reject(new Error('当前浏览器不支持音频播放'))
    }

    return gesturePlay
      .then(() => loadRingtone(el, KNOWN_RINGTONES[0], { audibilityTest }))
      .then(() => {
        keepaliveActive = true
        ringing = audibilityTest
        if (audibilityTest) {
          window.setTimeout(() => {
            if (player && keepaliveActive) {
              player.volume = KEEPALIVE_VOLUME
              ringing = false
            }
          }, 1200)
        }
        return true
      })
      .catch((err) => {
        keepaliveActive = false
        ringing = false
        const msg = err?.message || ''
        if (msg.includes('加载失败') || msg.includes('加载超时') || msg.includes('格式不支持')) {
          throw new Error('铃声加载失败，请确认 frontend/public/sounds 下有 happy.mp3 和 feeling.mp3')
        }
        throw new Error('启用失败，请使用 Chrome/Edge 并检查页面是否静音')
      })
  }

  async function playRingtone(filename) {
    if (!keepaliveActive) {
      throw new Error('请先点击「启用铃声」')
    }

    const el = getPlayer()
    const target = normalizeRingtone(filename)
    const url = ringtoneUrl(target)

    if (el.src !== url) {
      await loadRingtone(el, target, { audibilityTest: false })
    }

    el.loop = true
    el.volume = RING_VOLUME
    ringing = true
    await el.play()
    return true
  }

  async function retryRingtone(filename) {
    if (!keepaliveActive) {
      return unlockFromUserGesture({ audibilityTest: true })
    }
    if (filename) {
      return playRingtone(filename)
    }
    const el = getPlayer()
    el.volume = RING_VOLUME
    ringing = true
    await el.play()
    return true
  }

  function resumeKeepalive() {
    if (!player || !keepaliveActive) return
    ringing = false
    player.loop = true
    player.volume = KEEPALIVE_VOLUME
    player.play().catch(() => {})
  }

  /** 用户主动关闭闹钟：完全停止播放，但保留授权状态供下次闹钟使用 */
  function stopAlarmSound() {
    ringing = false
    if (player) {
      player.pause()
      player.currentTime = 0
    }
  }

  function reset() {
    if (player) {
      player.pause()
      player.removeAttribute('src')
      player.load()
    }
    player = null
    keepaliveActive = false
    ringing = false
    if (audioCtx) {
      audioCtx.close().catch(() => {})
      audioCtx = null
    }
  }

  function isUnlocked() {
    return keepaliveActive
  }

  function isRinging() {
    return ringing
  }

  return {
    getPlayer,
    preloadRingtone,
    unlockFromUserGesture,
    playRingtone,
    retryRingtone,
    stopAlarmSound,
    resumeKeepalive,
    reset,
    isUnlocked,
    isRinging
  }
}
