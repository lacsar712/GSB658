<template>
  <div style="display:flex; flex-direction:column; gap:24px; height:100%; position: relative; z-index: 10;">
    <!-- Page Header -->
    <div style="background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 20px; padding: 20px 24px; display: flex; align-items: center; gap: 16px; flex-shrink: 0; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);">
      <div style="width:48px; height:48px; border-radius:14px; background:linear-gradient(135deg, #60a5fa, #2dd4bf); display:flex; align-items:center; justify-content:center; font-size:22px; flex-shrink:0; box-shadow: 0 4px 12px rgba(96, 165, 250, 0.3);">🤖</div>
      <div style="flex:1;">
        <h2 style="font-size:18px; font-weight:900; color:white; margin:0; letter-spacing: -0.02em;">AI 问答管理中心</h2>
        <p style="font-size:12px; color:rgba(255,255,255,0.4); margin:4px 0 0;">查看问答记录、优化知识库标准答案、管理高频反馈</p>
      </div>
      <div style="display:flex; gap:16px; align-items:center;">
        <div style="text-align:center; padding:8px 16px; background:rgba(79,172,254,0.1); border-radius:12px; border:1px solid rgba(79,172,254,0.2);">
          <p style="font-size:20px; font-weight:900; color:#4facfe; margin:0;">{{ records.length }}</p>
          <p style="font-size:10px; color:rgba(255,255,255,0.4); margin:2px 0 0;">总问答记录</p>
        </div>
        <div style="text-align:center; padding:8px 16px; background:rgba(248,113,113,0.1); border-radius:12px; border:1px solid rgba(248,113,113,0.2);">
          <p style="font-size:20px; font-weight:900; color:#f87171; margin:0;">{{ records.filter((r: QARecord) => r.marked).length }}</p>
          <p style="font-size:10px; color:rgba(255,255,255,0.4); margin:2px 0 0;">待优化标注</p>
        </div>
      </div>
    </div>

    <!-- Content: Left = records list, Right = knowledge base -->
    <div style="display:grid; grid-template-columns:1fr 380px; gap:16px; flex:1; min-height:0;">
      
      <!-- Records List -->
      <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:20px; display:flex; flex-direction:column; gap:14px; overflow-y:auto;">
        <div style="display:flex; align-items:center; justify-content:space-between; flex-shrink:0;">
          <div style="display:flex; align-items:center; gap:8px;">
            <span style="font-size:16px;">💬</span>
            <h3 style="font-size:14px; font-weight:800; color:white; margin:0;">全部问答记录</h3>
          </div>
          <div style="display:flex; gap:8px;">
            <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
              :style="activeTab === tab.key 
                ? 'padding:5px 14px; border-radius:20px; background:rgba(79,172,254,0.3); color:#4facfe; border:1px solid rgba(79,172,254,0.4); font-size:12px; font-weight:700; cursor:pointer; font-family:inherit;'
                : 'padding:5px 14px; border-radius:20px; background:transparent; color:rgba(255,255,255,0.4); border:1px solid rgba(255,255,255,0.1); font-size:12px; font-weight:600; cursor:pointer; font-family:inherit;'">
              {{ tab.label }}
            </button>
          </div>
        </div>

        <div style="display:flex; flex-direction:column; gap:10px; overflow-y:auto;">
          <div v-for="record in filteredRecords" :key="record.id"
            style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.07); border-radius:14px; padding:14px 16px;">
            <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:12px;">
              <div style="flex:1;">
                <div style="display:flex; align-items:center; gap:8px; margin-bottom:8px;">
                  <span style="width:28px; height:28px; border-radius:8px; background:rgba(79,172,254,0.2); font-size:12px; display:flex; align-items:center; justify-content:center; flex-shrink:0;">👤</span>
                  <span style="font-size:11px; color:rgba(255,255,255,0.35);">用户 {{ record.userId }} · {{ record.time }}</span>
                  <span v-if="record.marked" style="font-size:10px; padding:2px 8px; border-radius:20px; background:rgba(248,113,113,0.15); color:#f87171; border:1px solid rgba(248,113,113,0.2);">⚠️ 待优化</span>
                </div>
                <p style="font-size:13px; color:rgba(255,255,255,0.8); margin:0 0 8px; font-weight:600;">Q: {{ record.question }}</p>
                <p style="font-size:12px; color:rgba(255,255,255,0.45); margin:0; line-height:1.5;">A: {{ record.answer }}</p>
              </div>
              <div style="display:flex; gap:6px; flex-shrink:0;">
                <button @click="record.marked = !record.marked"
                  :style="record.marked 
                    ? 'padding:5px 10px; border-radius:8px; background:rgba(248,113,113,0.2); color:#f87171; border:1px solid rgba(248,113,113,0.3); font-size:11px; cursor:pointer; font-family:inherit;'
                    : 'padding:5px 10px; border-radius:8px; background:rgba(255,255,255,0.05); color:rgba(255,255,255,0.4); border:1px solid rgba(255,255,255,0.1); font-size:11px; cursor:pointer; font-family:inherit;'">
                  {{ record.marked ? '取消标注' : '标注' }}
                </button>
                <button @click="addToKb(record)"
                  style="padding:5px 10px; border-radius:8px; background:rgba(79,172,254,0.15); color:#4facfe; border:1px solid rgba(79,172,254,0.2); font-size:11px; cursor:pointer; font-family:inherit;">
                  加入知识库
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Knowledge Base Panel -->
      <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:20px; display:flex; flex-direction:column; gap:14px; overflow:hidden;">
        <div style="display:flex; align-items:center; gap:8px; flex-shrink:0;">
          <span style="font-size:16px;">📚</span>
          <h3 style="font-size:14px; font-weight:800; color:white; margin:0;">标准知识库</h3>
        </div>
        <p style="font-size:12px; color:rgba(255,255,255,0.35); margin:0; flex-shrink:0;">点击「加入知识库」后可在此处编辑和更新标准答案</p>

        <div style="flex:1; overflow-y:auto; display:flex; flex-direction:column; gap:10px;">
          <div v-for="(kb, i) in knowledgeBase" :key="i"
            style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.06); border-radius:14px; padding:14px;">
            <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:8px;">
              <span style="font-size:11px; font-weight:700; color:#4facfe;">Q{{ (i as number) + 1 }}</span>
              <button @click="knowledgeBase.splice(i, 1)" style="background:transparent; border:none; color:rgba(248,113,113,0.6); cursor:pointer; font-size:14px;">✕</button>
            </div>
            <p style="font-size:12px; color:rgba(255,255,255,0.7); margin:0 0 8px; font-weight:600;">{{ kb.question }}</p>
            <textarea v-model="kb.answer" rows="3"
              style="width:100%; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:10px; padding:8px 10px; color:rgba(255,255,255,0.6); font-size:11px; resize:none; outline:none; font-family:inherit; box-sizing:border-box; line-height:1.5;"></textarea>
            <button @click="ElMessage.success('知识库已更新')" style="width:100%; margin-top:8px; padding:7px; border-radius:10px; background:rgba(79,172,254,0.15); color:#4facfe; border:1px solid rgba(79,172,254,0.2); font-size:12px; font-weight:700; cursor:pointer; font-family:inherit;">更新答案</button>
          </div>

          <div v-if="!knowledgeBase.length" style="padding:32px; text-align:center; color:rgba(255,255,255,0.2); font-size:13px;">
            暂无知识库条目<br/>从左侧问答记录点击「加入知识库」添加
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref<'all' | 'marked'>('all')
const tabs = [
  { key: 'all', label: '全部' },
  { key: 'marked', label: '仅待优化' },
]

interface QARecord { id: number; userId: number; question: string; answer: string; time: string; marked: boolean }

const records = ref<QARecord[]>([
  { id: 1, userId: 1001, question: '门禁卡怎么办理？', answer: '租客办理门禁卡需携带租房合同和身份证原件前往物业中心C栋101办理。', time: '今天 14:32', marked: false },
  { id: 2, userId: 1002, question: '外卖盒应该扔哪里？', answer: '外卖盒属于干垃圾，清洗后可归入可回收物；不可回收的部分归干垃圾。', time: '今天 13:18', marked: true },
  { id: 3, userId: 1003, question: '垃圾桶在哪里？', answer: '这是一个很好的问题，但我还在学习您的社区规章制度！', time: '昨天 20:45', marked: true },
  { id: 4, userId: 1001, question: '停车费怎么算？', answer: '业主月租200元/月；访客临停前2小时免费，之后5元/小时。', time: '昨天 17:22', marked: false },
  { id: 5, userId: 1004, question: '旧衣服怎么处理？', answer: '旧衣物可归入可回收物，也可投放至小区内的衣物回收箱进行捐赠再利用。', time: '3天前', marked: false },
])

const filteredRecords = computed(() =>
  activeTab.value === 'marked' ? records.value.filter((r: QARecord) => r.marked) : records.value
)

const knowledgeBase = ref<{ question: string; answer: string }[]>([
  { question: '废旧电池属于哪类垃圾？', answer: '废旧电池（含汞等有害物质）属于有害垃圾，需投放至红色有害垃圾桶，切勿混入其他垃圾。' }
])

const addToKb = (record: QARecord) => {
  if (knowledgeBase.value.find((k: { question: string }) => k.question === record.question)) {
    ElMessage.warning('该问题已在知识库中')
    return
  }
  knowledgeBase.value.push({ question: record.question, answer: record.answer })
  ElMessage.success('✅ 已加入知识库，请在右侧优化标准答案')
}
</script>
