<template>
  <div style="position:fixed; inset:0; display:flex; flex-direction:column; background:#f0f4ff; overflow:hidden;">
    
    <!-- Header -->
    <div style="flex-shrink:0; background:linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding:16px 20px; display:flex; align-items:center; gap:12px; box-shadow:0 2px 20px rgba(102,126,234,0.3);">
      <button @click="router.back()" style="width:36px; height:36px; border-radius:12px; background:rgba(255,255,255,0.15); border:none; color:white; cursor:pointer; display:flex; align-items:center; justify-content:center; flex-shrink:0;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
      </button>
      <div style="flex:1;">
        <h1 style="font-size:17px; font-weight:900; color:white; margin:0; line-height:1.2;">社区 AI 伴侣</h1>
        <p style="font-size:11px; color:rgba(255,255,255,0.65); margin:2px 0 0; display:flex; align-items:center; gap:4px;">
          <span style="width:6px; height:6px; border-radius:50%; background:#4ade80; display:inline-block; animation:blink 1.5s ease-in-out infinite;"></span>
          在线秒回
        </p>
      </div>
      <div style="width:40px; height:40px; border-radius:50%; background:rgba(255,255,255,0.2); display:flex; align-items:center; justify-content:center; font-size:20px;">🤖</div>
    </div>

    <!-- Chat Messages Area -->
    <div ref="chatScrollRef" style="flex:1; overflow-y:auto; padding:16px 14px; display:flex; flex-direction:column; gap:16px;">
      
      <div v-for="(msg, index) in messages" :key="index" :style="msg.isSelf ? 'display:flex; justify-content:flex-end;' : 'display:flex; justify-content:flex-start;'">
        
        <div :style="msg.isSelf ? 'display:flex; align-items:flex-end; gap:8px; flex-direction:row-reverse; max-width:80%;' : 'display:flex; align-items:flex-end; gap:8px; max-width:80%;'">
          <!-- Avatar -->
          <div :style="msg.isSelf ? 'width:34px; height:34px; border-radius:50%; background:#e8eaf6; display:flex; align-items:center; justify-content:center; font-size:16px; flex-shrink:0;' : 'width:34px; height:34px; border-radius:50%; background:linear-gradient(135deg, #667eea, #764ba2); display:flex; align-items:center; justify-content:center; font-size:16px; flex-shrink:0;'">
            {{ msg.isSelf ? '👤' : '🤖' }}
          </div>
          <!-- Bubble -->
          <div :style="msg.isSelf 
            ? 'padding:12px 16px; border-radius:20px 4px 20px 20px; background:linear-gradient(135deg, #667eea, #764ba2); color:white; font-size:14px; line-height:1.6; box-shadow:0 4px 16px rgba(102,126,234,0.3);'
            : 'padding:12px 16px; border-radius:4px 20px 20px 20px; background:white; color:#2d2d3a; font-size:14px; line-height:1.6; box-shadow:0 2px 12px rgba(0,0,0,0.08);'">
            {{ msg.content }}
            <div style="font-size:10px; margin-top:4px;" :style="msg.isSelf ? 'color:rgba(255,255,255,0.5); text-align:right;' : 'color:#ccc;'">{{ msg.time }}</div>
          </div>
        </div>
      </div>

      <!-- Typing indicator -->
      <div v-if="isTyping" style="display:flex; align-items:flex-end; gap:8px;">
        <div style="width:34px; height:34px; border-radius:50%; background:linear-gradient(135deg, #667eea, #764ba2); display:flex; align-items:center; justify-content:center; font-size:16px; flex-shrink:0;">🤖</div>
        <div style="padding:14px 18px; border-radius:4px 20px 20px 20px; background:white; box-shadow:0 2px 12px rgba(0,0,0,0.08); display:flex; align-items:center; gap:4px;">
          <span class="typing-dot"></span>
          <span class="typing-dot" style="animation-delay:0.2s;"></span>
          <span class="typing-dot" style="animation-delay:0.4s;"></span>
        </div>
      </div>
    </div>

    <!-- Quick Suggestion Chips -->
    <div v-if="messages.length <= 1" style="flex-shrink:0; padding:0 14px 10px; display:flex; gap:8px; overflow-x:auto; scrollbar-width:none;">
      <div v-for="chip in quickChips" :key="chip" @click="sendChip(chip)"
        style="flex-shrink:0; padding:7px 14px; background:white; border:1.5px solid #e8eaf6; border-radius:20px; font-size:12px; font-weight:600; color:#667eea; cursor:pointer; white-space:nowrap; box-shadow:0 2px 8px rgba(0,0,0,0.06); transition:all 0.2s;"
        @mouseenter="e => { e.currentTarget.style.background='#eef2ff'; e.currentTarget.style.borderColor='#667eea' }"
        @mouseleave="e => { e.currentTarget.style.background='white'; e.currentTarget.style.borderColor='#e8eaf6' }">
        {{ chip }}
      </div>
    </div>

    <!-- Input Area -->
    <div style="flex-shrink:0; background:white; padding:12px 14px 20px; border-top:1px solid rgba(0,0,0,0.06); display:flex; align-items:flex-end; gap:10px;">
      <div style="flex:1; background:#f8faff; border:1.5px solid #e8eaf6; border-radius:20px; padding:10px 16px; display:flex; align-items:center; transition:border-color 0.2s;"
        @focusin="e => e.currentTarget.style.borderColor='#667eea'"
        @focusout="e => e.currentTarget.style.borderColor='#e8eaf6'">
        <textarea v-model="inputRaw" placeholder="有什么可以帮您？" rows="1"
          @keydown.enter.exact.prevent="sendMessage"
          style="flex:1; border:none; outline:none; background:transparent; font-size:14px; color:#2d2d3a; resize:none; max-height:80px; font-family:inherit; line-height:1.5;"></textarea>
      </div>
      <button @click="sendMessage" :disabled="!inputRaw.trim() || isTyping"
        style="width:44px; height:44px; border-radius:50%; background:linear-gradient(135deg, #667eea, #764ba2); border:none; display:flex; align-items:center; justify-content:center; cursor:pointer; box-shadow:0 4px 16px rgba(102,126,234,0.4); transition:all 0.2s; flex-shrink:0;"
        :style="(!inputRaw.trim() || isTyping) ? 'opacity:0.4; cursor:not-allowed;' : ''"
        @mouseenter="e => { if(inputRaw.trim() && !isTyping) e.currentTarget.style.transform='scale(1.1)' }"
        @mouseleave="e => e.currentTarget.style.transform=''">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2.5"><path d="M22 2L11 13M22 2L15 22L11 13L2 9L22 2z"/></svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const inputRaw = ref('')
const isTyping = ref(false)
const chatScrollRef = ref<HTMLElement | null>(null)

const quickChips = ['门禁卡怎么办？', '垃圾投放时间', '物业电话是多少？', '停车费标准']

interface Message { content: string; isSelf: boolean; time?: string }

const messages = ref<Message[]>([
  { content: '你好呀！我是社区专属 AI 助手 🤖\n\n可以问我物业缴费、垃圾回收时间、报修流程等问题哦~', isSelf: false, time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) }
])

const scrollToBottom = async () => {
  await nextTick()
  if (chatScrollRef.value) chatScrollRef.value.scrollTop = chatScrollRef.value.scrollHeight
}

const sendChip = (chip: string) => {
  inputRaw.value = chip
  sendMessage()
}

const sendMessage = async () => {
  if (!inputRaw.value.trim() || isTyping.value) return
  const text = inputRaw.value.trim()
  inputRaw.value = ''
  messages.value.push({ content: text, isSelf: true, time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) })
  scrollToBottom()
  isTyping.value = true

  try {
    await new Promise(resolve => setTimeout(resolve, 1500))
    let reply = '这是一个很好的问题！我还在学习您的社区规章制度，请稍后联系物业中心了解详情 📞'
    if (text.includes('门禁') || text.includes('卡')) reply = '租客办理门禁卡需携带【租房合同】和【身份证原件】前往物业中心 C栋101 办理。\n\n办理时间：工作日 9:00-17:30 🏢'
    if (text.includes('垃圾')) reply = '我们社区实行定时定点分类投放：\n\n🗑️ 干湿垃圾：每日 7:00-9:00 及 18:00-20:00\n♻️ 可回收物：每周二、五 10:00-12:00\n\n在各楼栋指定回收点投放。'
    if (text.includes('物业')) reply = '物业服务中心电话：📞 400-888-8888\n\n服务时间：每天 8:00-22:00\n紧急故障：24小时值班'
    if (text.includes('停车')) reply = '🚗 停车收费标准：\n\n业主月租：200元/月\n访客临停：前2小时免费，之后5元/小时\n\n更多信息请咨询物业。'

    messages.value.push({ content: reply, isSelf: false, time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) })
  } catch {
    ElMessage.error('问答连接异常，请重试')
  } finally {
    isTyping.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.typing-dot {
  width: 7px;
  height: 7px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1s ease-in-out infinite;
  display: inline-block;
}
@keyframes typing {
  0%, 100% { transform: translateY(0); opacity: 0.4; }
  50% { transform: translateY(-5px); opacity: 1; }
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}
</style>
