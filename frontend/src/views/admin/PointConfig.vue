<template>
  <div style="display:flex; flex-direction:column; gap:24px; position: relative; z-index: 10;">
    <!-- Page Header -->
    <div style="background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 20px; padding: 20px 24px; display: flex; align-items: center; gap: 16px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);">
      <div style="width:48px; height:48px; border-radius:14px; background:linear-gradient(135deg, #6366f1, #8b5cf6); display:flex; align-items:center; justify-content:center; font-size:22px; flex-shrink:0; box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);">⚙️</div>
      <div>
        <h2 style="font-size:18px; font-weight:900; color:white; margin:0; letter-spacing: -0.02em;">积分规则配置中心</h2>
        <p style="font-size:12px; color:rgba(255,255,255,0.4); margin:4px 0 0;">可动态配置积分获取规则、奖励系数及临时活动政策</p>
      </div>
      <button @click="handleSaveAll" style="margin-left:auto; padding:10px 24px; border-radius:14px; background: linear-gradient(135deg, #6366f1, #8b5cf6); color:white; border:none; font-size:14px; font-weight:700; cursor:pointer; box-shadow:0 8px 16px rgba(99, 102, 241, 0.3); transition:all 0.25s;"
        @mouseenter="e => (e.currentTarget as HTMLElement).style.transform='translateY(-2px)'"
        @mouseleave="e => (e.currentTarget as HTMLElement).style.transform=''">
        保存全部配置
      </button>
    </div>

    <!-- Rules Grid: 3 columns -->
    <div style="display:grid; grid-template-columns:1fr 1fr 1fr; gap:16px;">
      
      <!-- Basic Points Card -->
      <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:20px; display:flex; flex-direction:column; gap:16px;">
        <div style="display:flex; align-items:center; gap:10px; margin-bottom:4px;">
          <span style="font-size:20px;">🎯</span>
          <h3 style="font-size:14px; font-weight:800; color:white; margin:0;">基础积分规则</h3>
        </div>
        <div v-for="rule in baseRules" :key="rule.key" style="display:flex; flex-direction:column; gap:6px;">
          <label style="font-size:12px; color:rgba(255,255,255,0.5); font-weight:600;">{{ rule.label }}</label>
          <div style="display:flex; align-items:center; gap:8px;">
            <input v-model.number="rule.value" type="number" :min="0" :max="rule.max"
              style="flex:1; background:rgba(255,255,255,0.07); border:1px solid rgba(255,255,255,0.12); border-radius:10px; padding:8px 12px; color:white; font-size:14px; font-weight:700; outline:none; font-family:inherit;"
              @focus="(e: FocusEvent) => (e.target as HTMLElement).style.borderColor='rgba(102,126,234,0.6)'"
              @blur="(e: FocusEvent) => (e.target as HTMLElement).style.borderColor='rgba(255,255,255,0.12)'" />
            <span style="font-size:12px; color:rgba(255,255,255,0.4); white-space:nowrap;">{{ rule.unit }}</span>
          </div>
        </div>
      </div>

      <!-- Bonus Multiplier Card -->
      <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:20px; display:flex; flex-direction:column; gap:16px;">
        <div style="display:flex; align-items:center; gap:10px; margin-bottom:4px;">
          <span style="font-size:20px;">🚀</span>
          <h3 style="font-size:14px; font-weight:800; color:white; margin:0;">奖励系数配置</h3>
        </div>
        <div v-for="rule in bonusRules" :key="rule.key" style="display:flex; flex-direction:column; gap:6px;">
          <label style="font-size:12px; color:rgba(255,255,255,0.5); font-weight:600;">{{ rule.label }}</label>
          <div style="display:flex; align-items:center; gap:8px;">
            <input v-model.number="rule.value" type="number" :min="1" :max="10" step="0.5"
              style="flex:1; background:rgba(255,255,255,0.07); border:1px solid rgba(255,255,255,0.12); border-radius:10px; padding:8px 12px; color:#fbbf24; font-size:14px; font-weight:700; outline:none; font-family:inherit;"
              @focus="(e: FocusEvent) => (e.target as HTMLElement).style.borderColor='rgba(251,191,36,0.6)'"
              @blur="(e: FocusEvent) => (e.target as HTMLElement).style.borderColor='rgba(255,255,255,0.12)'" />
            <span style="font-size:12px; color:rgba(255,255,255,0.4); white-space:nowrap;">{{ rule.unit }}</span>
          </div>
        </div>
      </div>

      <!-- Activity Rules Card -->
      <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:20px; display:flex; flex-direction:column; gap:16px;">
        <div style="display:flex; align-items:center; gap:10px; margin-bottom:4px;">
          <span style="font-size:20px;">🎉</span>
          <h3 style="font-size:14px; font-weight:800; color:white; margin:0;">临时活动设置</h3>
        </div>
        <div style="display:flex; flex-direction:column; gap:6px;">
          <label style="font-size:12px; color:rgba(255,255,255,0.5); font-weight:600;">活动名称</label>
          <input v-model="activity.name" type="text" placeholder="如：五一劳动节双倍积分"
            style="background:rgba(255,255,255,0.07); border:1px solid rgba(255,255,255,0.12); border-radius:10px; padding:8px 12px; color:white; font-size:13px; outline:none; font-family:inherit; width:100%; box-sizing:border-box;" />
        </div>
        <div style="display:flex; flex-direction:column; gap:6px;">
          <label style="font-size:12px; color:rgba(255,255,255,0.5); font-weight:600;">活动积分倍率</label>
          <div style="display:flex; align-items:center; gap:8px;">
            <input v-model.number="activity.multiplier" type="number" min="1" max="5" step="0.5"
              style="flex:1; background:rgba(255,255,255,0.07); border:1px solid rgba(255,255,255,0.12); border-radius:10px; padding:8px 12px; color:#f87171; font-size:14px; font-weight:700; outline:none; font-family:inherit;" />
            <span style="font-size:12px; color:rgba(255,255,255,0.4);">倍</span>
          </div>
        </div>
        <div style="display:flex; align-items:center; gap:10px; padding:10px 14px; background:rgba(255,255,255,0.04); border-radius:12px; border:1px solid rgba(255,255,255,0.07);">
          <label style="font-size:12px; color:rgba(255,255,255,0.5); font-weight:600; flex:1;">活动是否启用</label>
          <el-switch v-model="activity.enabled" active-color="#667eea" inactive-color="rgba(255,255,255,0.15)" />
        </div>
        <div v-if="activity.enabled" style="padding:10px 14px; background:rgba(102,126,234,0.1); border-radius:12px; border:1px solid rgba(102,126,234,0.2);">
          <p style="font-size:11px; color:#a5b4fc; margin:0; line-height:1.6;">
            🟢 活动已启用：所有答题、举报奖励将自动乘以 <strong>{{ activity.multiplier }}x</strong> 系数
          </p>
        </div>
      </div>
    </div>

    <!-- Streak Bonus Table -->
    <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:24px;">
      <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:20px;">
        <div style="display:flex; align-items:center; gap:10px;">
          <span style="font-size:20px;">🔥</span>
          <h3 style="font-size:14px; font-weight:800; color:white; margin:0;">连续打卡奖励阶梯</h3>
        </div>
        <span style="font-size:11px; color:rgba(255,255,255,0.3);">连续打卡天数越多，额外奖励越高</span>
      </div>
      <div style="display:grid; grid-template-columns:repeat(5, 1fr); gap:12px;">
        <div v-for="tier in streakTiers" :key="tier.days" style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.07); border-radius:14px; padding:14px 12px; text-align:center;">
          <div style="font-size:22px; margin-bottom:8px;">{{ tier.emoji }}</div>
          <p style="font-size:11px; color:rgba(255,255,255,0.4); margin:0 0 4px; font-weight:600;">{{ tier.days }}</p>
          <div style="display:flex; align-items:center; justify-content:center; gap:4px;">
            <input v-model.number="tier.bonus" type="number" min="0" max="100"
              style="width:50px; background:rgba(255,255,255,0.08); border:1px solid rgba(255,255,255,0.15); border-radius:8px; padding:5px 6px; color:#fbbf24; font-size:13px; font-weight:700; text-align:center; outline:none; font-family:inherit;" />
            <span style="font-size:10px; color:rgba(255,255,255,0.35);">分</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Current Config Preview -->
    <div style="background:rgba(255,255,255,0.03); border:1px solid rgba(255,255,255,0.06); border-radius:20px; padding:20px 24px;">
      <h3 style="font-size:13px; font-weight:700; color:rgba(255,255,255,0.5); margin:0 0 14px; letter-spacing:0.06em; text-transform:uppercase;">当前生效配置预览</h3>
      <div style="display:grid; grid-template-columns:repeat(4, 1fr); gap:12px;">
        <div v-for="preview in previewItems" :key="preview.label" style="background:rgba(255,255,255,0.05); border-radius:14px; padding:14px 16px;">
          <p style="font-size:11px; color:rgba(255,255,255,0.4); margin:0 0 4px; font-weight:600;">{{ preview.label }}</p>
          <p style="font-size:22px; font-weight:900; color:white; margin:0;">{{ preview.value }}<span style="font-size:12px; font-weight:400; color:rgba(255,255,255,0.4); margin-left:4px;">{{ preview.unit }}</span></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const baseRules = ref([
  { key: 'quiz', label: '每题答对基础积分', value: 5, max: 50, unit: '分/题' },
  { key: 'report', label: '举报审核通过奖励', value: 10, max: 100, unit: '分/次' },
  { key: 'firstLogin', label: '每日首次登录奖励', value: 2, max: 20, unit: '分/天' },
  { key: 'maxDaily', label: '每日积分上限', value: 50, max: 500, unit: '分' },
])

const bonusRules = ref([
  { key: 'consecutive3', label: '连续答对3题奖励', value: 1.5, max: 10, unit: '倍' },
  { key: 'firstTime', label: '首次完成任务奖励', value: 2.0, max: 10, unit: '倍' },
  { key: 'weekendBonus', label: '周末额外积分系数', value: 1.2, max: 5, unit: '倍' },
])

const activity = ref({ name: '春节期间限时双倍', multiplier: 2, enabled: false })

const streakTiers = ref([
  { days: '3天连续', bonus: 5, emoji: '🌱' },
  { days: '7天连续', bonus: 15, emoji: '🌿' },
  { days: '14天连续', bonus: 30, emoji: '🌳' },
  { days: '30天连续', bonus: 80, emoji: '🏆' },
  { days: '60天连续', bonus: 200, emoji: '👑' },
])

const previewItems = computed(() => [
  { label: '答题基础分', value: baseRules.value[0].value, unit: '分' },
  { label: '举报奖励', value: baseRules.value[1].value, unit: '分' },
  { label: '今日上限', value: baseRules.value[3].value, unit: '分' },
  { label: '活动系数', value: activity.value.enabled ? activity.value.multiplier + 'x 活跃' : '未启用', unit: '' },
])

const handleSaveAll = () => {
  ElMessage({ message: '✅ 积分规则已保存，实时生效！', type: 'success', duration: 2500 })
}
</script>
