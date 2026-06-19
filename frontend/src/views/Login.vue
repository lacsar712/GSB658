<template>
  <div style="position:fixed; inset:0; display:flex; align-items:center; justify-content:center; overflow:hidden; background:linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);">
    
    <!-- Animated orbs -->
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>
    <div class="orb orb-3"></div>

    <!-- Grid overlay -->
    <div style="position:absolute; inset:0; background-image: linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px); background-size: 50px 50px; pointer-events:none;"></div>

    <!-- Login Card -->
    <div style="position:relative; z-index:10; width:100%; max-width:400px; margin:0 16px;">
      <!-- Logo -->
      <div style="text-align:center; margin-bottom:32px;">
        <div style="display:inline-flex; align-items:center; justify-content:center; width:80px; height:80px; border-radius:24px; background:linear-gradient(135deg, #667eea, #764ba2); box-shadow:0 0 40px rgba(102,126,234,0.5); margin-bottom:16px; position:relative;">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" fill="rgba(255,255,255,0.9)"/>
            <path d="M9 22V12h6v10" fill="rgba(255,255,255,0.5)"/>
          </svg>
          <div style="position:absolute; top:-4px; right:-4px; width:14px; height:14px; border-radius:50%; background:#4ade80; animation:pulse-dot 2s infinite;"></div>
        </div>
        <h1 style="color:white; font-size:30px; font-weight:900; letter-spacing:-0.5px; margin:0;">社区智联</h1>
        <p style="color:rgba(255,255,255,0.45); font-size:13px; margin:8px 0 0 0;">智慧社区 · 智享生活</p>
      </div>

      <!-- Card -->
      <div style="padding:32px; border-radius:24px; background:rgba(255,255,255,0.07); backdrop-filter:blur(24px); border:1px solid rgba(255,255,255,0.12); box-shadow:0 32px 64px rgba(0,0,0,0.4);">
        <!-- Phone -->
        <div style="margin-bottom:20px;">
          <label style="display:block; color:rgba(255,255,255,0.55); font-size:11px; font-weight:700; letter-spacing:0.12em; text-transform:uppercase; margin-bottom:8px;">手机号码</label>
          <div style="position:relative;">
            <span style="position:absolute; left:14px; top:50%; transform:translateY(-50%); color:rgba(255,255,255,0.35); font-size:16px;">📱</span>
            <input v-model="form.phone" type="tel" placeholder="请输入手机号" class="dark-input" style="padding-left:44px;" />
          </div>
        </div>

        <!-- Code -->
        <div style="margin-bottom:28px;">
          <label style="display:block; color:rgba(255,255,255,0.55); font-size:11px; font-weight:700; letter-spacing:0.12em; text-transform:uppercase; margin-bottom:8px;">短信验证码</label>
          <div style="display:flex; gap:10px;">
            <div style="position:relative; flex:1;">
              <span style="position:absolute; left:14px; top:50%; transform:translateY(-50%); color:rgba(255,255,255,0.35); font-size:16px;">🔒</span>
              <input v-model="form.code" type="text" placeholder="6 位验证码" class="dark-input" style="padding-left:44px;" />
            </div>
            <button @click="sendCode" :disabled="countdown > 0" class="code-btn" :style="countdown > 0 ? 'opacity:0.5;cursor:not-allowed;' : ''">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </button>
          </div>
        </div>

        <!-- Login Button -->
        <button @click="handleLogin" :disabled="loading" class="login-btn">
          <span v-if="!loading">立即登录 →</span>
          <span v-else style="display:flex; align-items:center; justify-content:center; gap:8px;">
            <div style="width:18px; height:18px; border:2px solid rgba(255,255,255,0.3); border-top-color:white; border-radius:50; animation:spin 0.8s linear infinite;"></div>
            登录中...
          </span>
        </button>


      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const countdown = ref(0)
const form = reactive({ phone: '', code: '' })

const sendCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请先输入正确的 11 位手机号')
    return
  }
  try {
    await request.post('/auth/sms-code', { phone: form.phone })
    ElMessage.success('验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => { countdown.value--; if (countdown.value <= 0) clearInterval(timer) }, 1000)
  } catch {}
}

const handleLogin = async () => {
  if (!form.phone || !form.code) { ElMessage.warning('请填写手机号和验证码'); return }
  loading.value = true
  try {
    const response = await request.post('/auth/login', form) as any
    // 后端返回 { token, role }
    const resData = response.data.data
    const token = resData?.token ?? resData
    const role = resData?.role ?? 'USER'
    localStorage.setItem('token', token as string)
    localStorage.setItem('role', role as string)
    ElMessage.success('登录成功！')
    // 管理员直接跳转后台
    if (role === 'ADMIN') {
      router.push('/admin/dashboard')
    } else {
      router.push('/')
    }
  } catch {} finally {
    loading.value = false
  }
}
</script>

<style scoped>
.orb { position: absolute; border-radius: 50%; filter: blur(80px); animation: orbFloat 8s ease-in-out infinite; pointer-events: none; }
.orb-1 { width: 400px; height: 400px; background: radial-gradient(circle, rgba(102,126,234,0.45), transparent); top: -120px; right: -100px; }
.orb-2 { width: 300px; height: 300px; background: radial-gradient(circle, rgba(118,75,162,0.4), transparent); bottom: -80px; left: -80px; animation-delay: -3s; }
.orb-3 { width: 200px; height: 200px; background: radial-gradient(circle, rgba(240,147,251,0.3), transparent); top: 40%; left: 30%; animation-delay: -5s; }

@keyframes orbFloat {
  0%, 100% { transform: translate(0,0) scale(1); }
  33% { transform: translate(30px,-50px) scale(1.1); }
  66% { transform: translate(-20px,20px) scale(0.9); }
}

.dark-input {
  width: 100%;
  height: 50px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 14px;
  color: white;
  font-size: 15px;
  padding: 0 16px;
  box-sizing: border-box;
  outline: none;
  transition: all 0.3s;
  font-family: inherit;
}
.dark-input::placeholder { color: rgba(255,255,255,0.3); }
.dark-input:focus { border-color: rgba(102,126,234,0.8); background: rgba(255,255,255,0.12); box-shadow: 0 0 0 3px rgba(102,126,234,0.15); }

.code-btn {
  height: 50px;
  padding: 0 16px;
  border-radius: 14px;
  background: rgba(102,126,234,0.25);
  border: 1px solid rgba(102,126,234,0.4);
  color: #a5b4fc;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  font-family: inherit;
}
.code-btn:hover:not(:disabled) { background: rgba(102,126,234,0.45); }

.login-btn {
  width: 100%;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: white;
  font-size: 17px;
  font-weight: 800;
  cursor: pointer;
  letter-spacing: 0.5px;
  box-shadow: 0 8px 32px rgba(102,126,234,0.5);
  transition: all 0.3s;
  font-family: inherit;
}
.login-btn:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 12px 40px rgba(102,126,234,0.65); }
.login-btn:active { transform: translateY(0); }

@keyframes pulse-dot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.2); }
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
