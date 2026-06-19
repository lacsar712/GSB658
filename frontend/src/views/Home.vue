<template>
  <div style="position:fixed; inset:0; display:flex; flex-direction:column; background:linear-gradient(160deg, #f0f4ff 0%, #faf5ff 50%, #fff5f5 100%); overflow:hidden;">
    
    <!-- Scrollable Content Area -->
    <div style="flex:1; overflow-y:auto; padding-bottom:80px;">
      
      <!-- Header -->
      <header style="position:relative; overflow:hidden; background:linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius:0 0 36px 36px; padding:20px 20px 28px; color:white;">
        <!-- Decorative circles -->
        <div style="position:absolute; width:200px; height:200px; background:rgba(255,255,255,0.07); border-radius:50%; top:-60px; right:-40px; pointer-events:none;"></div>
        <div style="position:absolute; width:100px; height:100px; background:rgba(255,255,255,0.05); border-radius:50%; bottom:-30px; left:20px; pointer-events:none;"></div>

        <div style="position:relative; z-index:2;">
          <!-- Top row: user info + logout -->
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
            <div>
              <p style="font-size:13px; opacity:0.7; margin:0;">{{ greeting }}，</p>
              <h1 style="font-size:22px; font-weight:900; margin:4px 0 0; letter-spacing:-0.5px;">{{ userInfo.nickname }} 👋</h1>
              <p style="font-size:11px; opacity:0.55; margin:4px 0 0; display:flex; align-items:center; gap:4px;">
                📍 {{ userInfo.communityName }}
                <button v-if="!userInfo.communityId" @click="showBinding = true" style="margin-left:4px; padding:2px 6px; border-radius:4px; background:rgba(255,255,255,0.2); border:none; color:white; font-size:10px; cursor:pointer;">去绑定</button>
              </p>
            </div>
            <div style="display:flex; align-items:center; gap:8px;">
              <button @click="handleLogout" style="width:38px; height:38px; border-radius:12px; background:rgba(255,255,255,0.18); border:none; color:white; cursor:pointer; display:flex; align-items:center; justify-content:center;" title="退出登录">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/></svg>
              </button>
              <div style="width:44px; height:44px; border-radius:16px; background:rgba(255,255,255,0.22); display:flex; align-items:center; justify-content:center; font-weight:900; font-size:18px;">
                {{ (userInfo.nickname || 'U')[0] }}
              </div>
            </div>
          </div>

          <!-- Points Card -->
          <div style="background:rgba(255,255,255,0.15); backdrop-filter:blur(20px); border:1px solid rgba(255,255,255,0.25); border-radius:20px; padding:16px 20px;">
            <div style="display:flex; justify-content:space-between; align-items:center;">
              <div style="flex:1;">
                <p style="font-size:10px; font-weight:700; letter-spacing:0.1em; opacity:0.65; margin:0; text-transform:uppercase;">我的积分余额</p>
                <div style="display:flex; align-items:flex-end; gap:6px; margin-top:4px;">
                  <span style="font-size:38px; font-weight:900; line-height:1;">{{ userInfo.pointBalance }}</span>
                  <span style="font-size:13px; opacity:0.65; padding-bottom:6px;">积分</span>
                </div>
                <div style="margin-top:8px; display:flex; gap:6px; flex-wrap:wrap;">
                  <span v-if="userInfo.consecutiveDays > 0" style="font-size:11px; padding:3px 10px; border-radius:20px; background:rgba(255,255,255,0.2); font-weight:600;">
                    🔥 连续打卡 {{ userInfo.consecutiveDays }} 天
                  </span>
                  <span v-if="getStreakBonus(userInfo.consecutiveDays)" style="font-size:11px; padding:3px 10px; border-radius:20px; background:rgba(255,215,0,0.3); font-weight:700; color:rgba(255,255,255,0.95);">
                    ⚡ {{ getStreakBonus(userInfo.consecutiveDays) }} 奖励
                  </span>
                </div>
              </div>
              <button @click="router.push('/point-mall')" style="padding:12px 20px; border-radius:16px; background:white; color:#667eea; border:none; font-size:14px; font-weight:800; cursor:pointer; box-shadow:0 4px 20px rgba(0,0,0,0.15); white-space:nowrap;">
                去兑换 →
              </button>
            </div>
          </div>
        </div>
      </header>

      <!-- Content -->
      <main style="padding:20px 16px 0;">

        <!-- Quick Actions Title -->
        <div style="display:flex; align-items:center; gap:8px; margin-bottom:14px;">
          <div style="width:4px; height:20px; border-radius:4px; background:linear-gradient(to bottom, #667eea, #764ba2);"></div>
          <h2 style="font-size:17px; font-weight:900; color:#1a1a2e; margin:0;">快捷功能</h2>
        </div>

        <!-- Quick Action Cards: 2 columns -->
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px; margin-bottom:24px;">
          
          <!-- Report Card -->
          <div @click="showReport = true" class="quick-action-card" style="background:linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%); border-radius:20px; padding:18px 16px; cursor:pointer; box-shadow:0 8px 24px rgba(238,90,36,0.35); transition:transform 0.2s, box-shadow 0.2s; position:relative; overflow:hidden;">
            <div style="position:absolute; width:80px; height:80px; background:rgba(255,255,255,0.1); border-radius:50%; top:-20px; right:-20px; pointer-events:none;"></div>
            <div style="font-size:32px; margin-bottom:10px; position:relative; z-index:1;">🚨</div>
            <h3 style="font-size:15px; font-weight:800; color:white; margin:0 0 4px; position:relative; z-index:1;">违规举报</h3>
            <p style="font-size:11px; color:rgba(255,255,255,0.75); margin:0; position:relative; z-index:1;">发现问题立即上报</p>
            <div style="position:absolute; right:14px; bottom:14px; width:28px; height:28px; border-radius:8px; background:rgba(255,255,255,0.2); display:flex; align-items:center; justify-content:center; z-index:1;">
              <span style="color:white; font-size:14px;">›</span>
            </div>
          </div>

          <!-- AI Chat Card -->
          <div @click="router.push('/ai-chat')" class="quick-action-card" style="background:linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius:20px; padding:18px 16px; cursor:pointer; box-shadow:0 8px 24px rgba(102,126,234,0.35); transition:transform 0.2s, box-shadow 0.2s; position:relative; overflow:hidden;">
            <div style="position:absolute; width:80px; height:80px; background:rgba(255,255,255,0.1); border-radius:50%; top:-20px; right:-20px; pointer-events:none;"></div>
            <div style="font-size:32px; margin-bottom:10px; position:relative; z-index:1;">🤖</div>
            <h3 style="font-size:15px; font-weight:800; color:white; margin:0 0 4px; position:relative; z-index:1;">AI 小助手</h3>
            <p style="font-size:11px; color:rgba(255,255,255,0.75); margin:0; position:relative; z-index:1;">社区百科随时问</p>
            <div style="position:absolute; right:14px; bottom:14px; width:28px; height:28px; border-radius:8px; background:rgba(255,255,255,0.2); display:flex; align-items:center; justify-content:center; z-index:1;">
              <span style="color:white; font-size:14px;">›</span>
            </div>
          </div>

          <!-- Point Mall Card -->
          <div @click="router.push('/point-mall')" class="quick-action-card" style="background:linear-gradient(135deg, #f093fb 0%, #f5576c 100%); border-radius:20px; padding:18px 16px; cursor:pointer; box-shadow:0 8px 24px rgba(245,87,108,0.35); transition:transform 0.2s, box-shadow 0.2s; position:relative; overflow:hidden;">
            <div style="position:absolute; width:80px; height:80px; background:rgba(255,255,255,0.1); border-radius:50%; top:-20px; right:-20px; pointer-events:none;"></div>
            <div style="font-size:32px; margin-bottom:10px; position:relative; z-index:1;">🛍️</div>
            <h3 style="font-size:15px; font-weight:800; color:white; margin:0 0 4px; position:relative; z-index:1;">积分商城</h3>
            <p style="font-size:11px; color:rgba(255,255,255,0.75); margin:0; position:relative; z-index:1;">积分兑换好礼</p>
            <div style="position:absolute; right:14px; bottom:14px; width:28px; height:28px; border-radius:8px; background:rgba(255,255,255,0.2); display:flex; align-items:center; justify-content:center; z-index:1;">
              <span style="color:white; font-size:14px;">›</span>
            </div>
          </div>

          <!-- Notice Card -->
          <div @click="showNotice = true" class="quick-action-card" style="background:linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); border-radius:20px; padding:18px 16px; cursor:pointer; box-shadow:0 8px 24px rgba(79,172,254,0.35); transition:transform 0.2s, box-shadow 0.2s; position:relative; overflow:hidden;">
            <div style="position:absolute; width:80px; height:80px; background:rgba(255,255,255,0.1); border-radius:50%; top:-20px; right:-20px; pointer-events:none;"></div>
            <div style="font-size:32px; margin-bottom:10px; position:relative; z-index:1;">📢</div>
            <h3 style="font-size:15px; font-weight:800; color:white; margin:0 0 4px; position:relative; z-index:1;">社区公告</h3>
            <p style="font-size:11px; color:rgba(255,255,255,0.75); margin:0; position:relative; z-index:1;">最新通知查看</p>
            <div style="position:absolute; right:14px; bottom:14px; width:28px; height:28px; border-radius:8px; background:rgba(255,255,255,0.2); display:flex; align-items:center; justify-content:center; z-index:1;">
              <span style="color:white; font-size:14px;">›</span>
            </div>
          </div>
        </div>

        <!-- Daily Quiz Section -->
        <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:14px;">
          <div style="display:flex; align-items:center; gap:8px;">
            <div style="width:4px; height:20px; border-radius:4px; background:linear-gradient(to bottom, #667eea, #764ba2);"></div>
            <h2 style="font-size:17px; font-weight:900; color:#1a1a2e; margin:0;">每日积分打卡</h2>
          </div>
          <span style="font-size:11px; padding:4px 12px; border-radius:20px; background:#eef2ff; color:#667eea; font-weight:700;">🎯 答对 +5分</span>
        </div>

        <!-- Loading -->
        <div v-if="loadingQuiz" style="display:flex; flex-direction:column; gap:12px;">
          <div v-for="i in 2" :key="i" style="border-radius:20px; background:white; height:140px; box-shadow:0 4px 20px rgba(0,0,0,0.06); opacity:0.6; animation: pulse 1.5s ease-in-out infinite;"></div>
        </div>

        <!-- Completed -->
        <div v-else-if="!quizzes.length" style="border-radius:20px; padding:32px; text-align:center; background:white; box-shadow:0 4px 20px rgba(0,0,0,0.06);">
          <div style="font-size:48px; margin-bottom:12px;">🎉</div>
          <p style="font-weight:800; color:#1a1a2e; font-size:16px; margin:0;">今日答题已完成！</p>
          <p style="font-size:13px; color:#999; margin:6px 0 0;">明天再来继续赚积分吧</p>
        </div>

        <!-- Quiz Cards -->
        <div v-else style="display:flex; flex-direction:column; gap:14px;">
          <div v-for="(quiz, index) in quizzes" :key="quiz.id" style="border-radius:20px; background:white; box-shadow:0 4px 20px rgba(0,0,0,0.07); overflow:hidden;">
            <!-- Quiz Header -->
            <div style="padding:16px 16px 12px; border-bottom:1px solid #f3f4f6;">
              <div style="display:flex; align-items:flex-start; gap:10px;">
                <span style="width:28px; height:28px; border-radius:10px; background:linear-gradient(135deg, #667eea, #764ba2); color:white; font-size:11px; font-weight:900; display:flex; align-items:center; justify-content:center; flex-shrink:0;">Q{{ index + 1 }}</span>
                <p style="font-size:14px; font-weight:600; color:#2d2d3a; line-height:1.6; margin:0; padding-top:3px;">{{ quiz.content }}</p>
              </div>
            </div>
            <!-- Options -->
            <div style="padding:12px 14px; display:flex; flex-direction:column; gap:8px;">
              <button v-for="opt in parseOptions(quiz.optionsJson)" :key="opt.key"
                @click="submitQuiz(quiz.id, opt.key)"
                style="width:100%; text-align:left; padding:11px 14px; border-radius:14px; border:1.5px solid #e8eaf6; background:white; font-size:13px; color:#444; cursor:pointer; font-family:inherit; display:flex; align-items:center; gap:8px; transition:all 0.2s;"
                @mouseenter="e => { e.currentTarget.style.borderColor='#667eea'; e.currentTarget.style.background='#f0f0ff'; e.currentTarget.style.color='#667eea' }"
                @mouseleave="e => { e.currentTarget.style.borderColor='#e8eaf6'; e.currentTarget.style.background='white'; e.currentTarget.style.color='#444' }">
                <span style="width:24px; height:24px; border-radius:8px; background:linear-gradient(135deg, #667eea, #764ba2); color:white; font-size:11px; font-weight:900; display:flex; align-items:center; justify-content:center; flex-shrink:0;">{{ opt.key }}</span>
                <span style="font-weight:500;">{{ opt.val }}</span>
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Bottom Navigation Bar (fixed at bottom, always horizontal) -->
    <nav style="position:absolute; bottom:0; left:0; right:0; height:70px; background:rgba(255,255,255,0.95); backdrop-filter:blur(20px); border-top:1px solid rgba(0,0,0,0.07); display:flex; align-items:center; justify-content:space-around; padding:0 4px 8px; z-index:50; flex-shrink:0;">
      
      <div @click="router.push('/')" style="display:flex; flex-direction:column; align-items:center; gap:2px; cursor:pointer; padding:6px 14px; border-radius:14px; background:#eef2ff;">
        <span style="font-size:22px; line-height:1;">🏠</span>
        <span style="font-size:10px; font-weight:700; color:#667eea;">首页</span>
      </div>

      <div @click="router.push('/point-mall')" style="display:flex; flex-direction:column; align-items:center; gap:2px; cursor:pointer; padding:6px 14px; border-radius:14px;">
        <span style="font-size:22px; line-height:1;">🛍️</span>
        <span style="font-size:10px; font-weight:600; color:#888;">商城</span>
      </div>

      <div @click="router.push('/orders')" style="display:flex; flex-direction:column; align-items:center; gap:2px; cursor:pointer; padding:6px 14px; border-radius:14px;">
        <span style="font-size:22px; line-height:1;">📦</span>
        <span style="font-size:10px; font-weight:600; color:#888;">订单</span>
      </div>

      <div @click="router.push('/ai-chat')" style="display:flex; flex-direction:column; align-items:center; gap:2px; cursor:pointer; padding:6px 14px; border-radius:14px;">
        <span style="font-size:22px; line-height:1;">💬</span>
        <span style="font-size:10px; font-weight:600; color:#888;">问答</span>
      </div>

      <div @click="handleAdminNav" style="display:flex; flex-direction:column; align-items:center; gap:2px; cursor:pointer; padding:6px 14px; border-radius:14px;">
        <span style="font-size:22px; line-height:1;">⚙️</span>
        <span :style="isAdmin ? 'font-size:10px; font-weight:600; color:#667eea;' : 'font-size:10px; font-weight:600; color:#888;'">管理</span>
      </div>
    </nav>

    <!-- Notice Drawer -->
    <el-drawer v-model="showNotice" :with-header="false" size="70%" direction="btt" style="border-radius: 24px 24px 0 0;">
      <div style="padding:24px;">
        <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:20px;">
          <div>
            <h3 style="font-size:20px; font-weight:900; color:#1a1a2e; margin:0;">📢 社区公告</h3>
            <p style="font-size:12px; color:#999; margin:4px 0 0;">最新通知 · 实时更新</p>
          </div>
          <button @click="showNotice = false" style="width:36px; height:36px; border-radius:50%; background:#f5f5f5; border:none; cursor:pointer; font-size:16px;">✕</button>
        </div>
        <div style="display:flex; flex-direction:column; gap:12px;">
          <div v-for="(notice, i) in noticeList" :key="i" style="background:#f8faff; border-radius:16px; padding:16px; border-left:4px solid #4facfe;">
            <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:6px;">
              <span style="font-size:13px; font-weight:800; color:#1a1a2e;">{{ notice.title }}</span>
              <span style="font-size:10px; color:#aaa; background:#eee; padding:2px 8px; border-radius:20px;">{{ notice.date }}</span>
            </div>
            <p style="font-size:12px; color:#666; margin:0; line-height:1.6;">{{ notice.content }}</p>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- Report Drawer -->
    <el-drawer v-model="showReport" :with-header="false" size="88%" direction="btt" style="border-radius: 24px 24px 0 0;">
      <div style="padding:24px;">
        <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:24px;">
          <div>
            <h3 style="font-size:20px; font-weight:900; color:#1a1a2e; margin:0;">提交违规举报</h3>
            <p style="font-size:12px; color:#999; margin:4px 0 0;">举报将立即推送给社区管理员</p>
          </div>
          <button @click="showReport = false" style="width:36px; height:36px; border-radius:50%; background:#f5f5f5; border:none; cursor:pointer; font-size:16px; display:flex; align-items:center; justify-content:center;">✕</button>
        </div>
        <el-form label-position="top">
          <el-form-item label="问题照片">
            <input ref="photoInput" type="file" accept="image/*" style="display:none;" @change="handlePhotoSelect" />
            <div @click="() => photoInput?.click()" style="width:100%; height:140px; border-radius:16px; border:2px dashed #ddd; display:flex; flex-direction:column; align-items:center; justify-content:center; gap:8px; cursor:pointer; color:#999; position:relative; overflow:hidden;">
              <img v-if="reportForm.photoPreview" :src="reportForm.photoPreview" style="position:absolute; inset:0; width:100%; height:100%; object-fit:cover;" />
              <template v-else>
                <div style="font-size:32px;">📷</div>
                <span style="font-size:13px;">点此拍摄或选择照片</span>
              </template>
            </div>
          </el-form-item>
          <el-form-item label="当前位置">
            <el-input v-model="reportForm.locationDetail" readonly>
              <template #prefix>📍</template>
              <template #suffix>
                <el-button link @click="refreshLocation">刷新</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="详情描述">
            <el-input v-model="reportForm.description" type="textarea" :rows="3" placeholder="描述问题详情..." />
          </el-form-item>
        </el-form>
        <button @click="submitFakeReport" style="width:100%; height:52px; border-radius:16px; background:linear-gradient(135deg, #ff6b6b, #ee5a24); color:white; border:none; font-size:16px; font-weight:800; cursor:pointer; box-shadow:0 8px 24px rgba(238,90,36,0.4); margin-top:8px; font-family:inherit;">
          立即提交举报
        </button>
      </div>
    </el-drawer>

    <!-- Community Binding Drawer -->
    <el-drawer v-model="showBinding" title="申请绑定社区" direction="btt" size="85%" :with-header="false" style="border-radius:32px 32px 0 0;">
      <div style="padding:24px 20px;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
          <h2 style="font-size:20px; font-weight:900; color:#1a1a2e; margin:0;">真实居民认证</h2>
          <button @click="showBinding = false" style="background:none; border:none; color:#999; font-size:24px;">×</button>
        </div>
        
        <el-form label-position="top">
          <el-form-item label="认领社区">
            <el-select v-model="bindingForm.communityId" placeholder="请选择所属社区" style="width:100%;">
              <el-option label="碧桂园凤凰城" :value="1001" />
              <el-option label="万科魅力之城" :value="1002" />
              <el-option label="恒大名都" :value="1003" />
            </el-select>
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="bindingForm.realName" placeholder="用于身份核实" />
          </el-form-item>
          <el-form-item label="详细住址">
            <el-input v-model="bindingForm.addressDetail" placeholder="几号楼几单元几室" />
          </el-form-item>
          <el-form-item label="证明材料 (房产证/租赁合同等)">
            <input ref="proveInput" type="file" accept="image/*" style="display:none;" @change="handleProveSelect" />
            <div @click="() => proveInput?.click()" style="width:100%; height:120px; border-radius:16px; border:2px dashed #ddd; display:flex; flex-direction:column; align-items:center; justify-content:center; gap:8px; cursor:pointer; color:#999; position:relative; overflow:hidden;">
              <img v-if="bindingForm.provePhoto" :src="bindingForm.provePhoto" style="position:absolute; inset:0; width:100%; height:100%; object-fit:cover;" />
              <span v-else>📷 上传证明图片</span>
            </div>
          </el-form-item>
          
          <button @click="submitBinding" style="width:100%; margin-top:20px; padding:14px; border-radius:18px; background:linear-gradient(135deg, #667eea, #764ba2); color:white; border:none; font-size:16px; font-weight:800; box-shadow:0 8px 24px rgba(102,126,234,0.3); cursor:pointer;">
            提交认证申请
          </button>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useDailyQuiz } from '@/composables/useDailyQuiz'

const router = useRouter()
const isAdmin = localStorage.getItem('role') === 'ADMIN'

const {
  loadingQuiz,
  quizzes,
  getStreakBonus,
  parseOptions,
  loadTodayQuizzes,
  submitQuiz: submitQuizComposable
} = useDailyQuiz()

const handleAdminNav = () => {
  if (isAdmin) {
    router.push('/admin/dashboard')
  } else {
    ElMessage({ message: '🔒 暂无管理员权限，请使用管理员账号登录', type: 'warning', duration: 2500 })
  }
}
const userInfo = ref({
  nickname: '居民用户',
  communityName: '正在加载...',
  pointBalance: 0,
  consecutiveDays: 0,
  communityId: null
})
const showReport = ref(false)
const showNotice = ref(false)
const photoInput = ref<HTMLInputElement>()
const reportForm = ref({
  photoUrl: '',
  photoPreview: '',
  locationDetail: '上海市浦东新区世纪大道 1 号',
  description: ''
})

// 社区绑定相关
const showBinding = ref(false)
const proveInput = ref<HTMLInputElement>()
const bindingForm = ref({
  communityId: null,
  realName: '',
  addressDetail: '',
  provePhoto: ''
})

const handleProveSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = (e) => {
    bindingForm.value.provePhoto = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

const submitBinding = async () => {
  if (!bindingForm.value.communityId || !bindingForm.value.realName || !bindingForm.value.provePhoto) {
    ElMessage.warning('请完整填写申请信息')
    return
  }
  try {
    const res = await request.post('/binding/submit', {
      communityId: bindingForm.value.communityId,
      communityName: '碧桂园凤凰城', // 简化，实际应从选择项获取
      realName: bindingForm.value.realName,
      addressDetail: bindingForm.value.addressDetail,
      provePhoto: bindingForm.value.provePhoto
    })
    if (res.data.code === 200) {
      ElMessage.success('认证申请已提交，请等待管理员审核')
      showBinding.value = false
    }
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await request.get('/user/profile')
    if (response.data.code === 200) {
      const data = response.data.data
      userInfo.value = {
        nickname: data.nickname || '居民用户',
        communityName: data.communityId ? getCommunityName(data.communityId) : '未绑定社区',
        pointBalance: data.pointBalance || 0,
        consecutiveDays: data.consecutiveDays || 0,
        communityId: data.communityId
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 模拟获取社区名称
const getCommunityName = (id: number) => {
  const map: any = { 1001: '碧桂园凤凰城', 1002: '万科魅力之城', 1003: '恒大名都' }
  return map[id] || '未知社区'
}

const noticeList = ref([
  { title: '🔧 小区停水通知', date: '2月24日', content: '因市政管网维修，本社区将于2月26日 08:00-18:00 临时停水，请居民提前储水，不便之处敬请谅解。' },
  { title: '🏡 业主大会召集令', date: '2月20日', content: '定于3月1日（周六）下午2时，在社区活动中心二楼会议室召开业主大会，欢迎各位业主积极参与。' },
  { title: '🚗 地下车位管理新规', date: '2月15日', content: '自3月起，访客车辆限停4小时，超时按每小时5元收费。请各业主告知来访亲友。' },
])

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 12) return '早安'
  if (h < 14) return '午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

onMounted(() => {
  // 加载用户信息
  loadUserInfo()
  
  // 加载答题
  loadTodayQuizzes()
})

const submitQuiz = (qId: number, answerKey: string) => {
  submitQuizComposable(qId, answerKey, (result) => {
    if (result.earnedPoints > 0) {
      userInfo.value.pointBalance = (userInfo.value.pointBalance as number) + result.earnedPoints
      userInfo.value.consecutiveDays = result.consecutiveDays
    }
  })
}

const handlePhotoSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小（限制5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }

  // 创建预览
  const reader = new FileReader()
  reader.onload = (e) => {
    reportForm.value.photoPreview = e.target?.result as string
    reportForm.value.photoUrl = e.target?.result as string // 实际项目中应上传到服务器获取URL
  }
  reader.readAsDataURL(file)
}

const refreshLocation = () => {
  // 实际项目中应调用地理定位API
  ElMessage.success('定位已刷新')
}

const submitFakeReport = async () => {
  // 验证表单
  if (!reportForm.value.photoUrl) {
    ElMessage.warning('请先上传问题照片')
    return
  }
  if (!reportForm.value.description.trim()) {
    ElMessage.warning('请填写详情描述')
    return
  }

  try {
    const response = await request.post('/report/submit', {
      photoUrl: reportForm.value.photoUrl,
      locationDetail: reportForm.value.locationDetail,
      description: reportForm.value.description,
      longitude: 121.522089, // Default mock longitude
      latitude: 31.229158    // Default mock latitude
    })
    
    if (response.data.code === 200) {
      ElMessage.success('🎉 举报已提交，感谢为社区出力！')
      // 重置表单
      reportForm.value = {
        photoUrl: '',
        photoPreview: '',
        locationDetail: '上海市浦东新区世纪大道 1 号',
        description: ''
      }
      showReport.value = false
    } else {
      ElMessage.error(response.data.message || '提交失败，请重试')
    }
  } catch (error: any) {
    console.error('提交举报失败:', error)
    ElMessage.error(error.response?.data?.message || '网络错误，请稍后重试')
  }
}

const handleLogout = async () => {
  await ElMessageBox.confirm('确认退出登录？', '提示', { confirmButtonText: '退出', cancelButtonText: '取消', type: 'warning' })
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<style scoped>
.quick-action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(0,0,0,0.2) !important;
}
.quick-action-card:active {
  transform: scale(0.97);
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 0.3; }
}
</style>
