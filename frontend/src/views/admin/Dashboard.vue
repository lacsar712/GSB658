<template>
  <div style="display:flex; flex-direction:column; gap:20px; position: relative; z-index: 10;">
    <!-- Top Stats -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in statsCards" :key="item.title">
        <div class="stats-card">
          <div style="display:flex; justify-content:space-between; align-items:flex-start;">
            <div>
              <p class="stats-title">{{ item.title }}</p>
              <h3 class="stats-value">{{ item.value }}</h3>
            </div>
            <div class="stats-icon" :style="{ background: item.color }">{{ item.icon }}</div>
          </div>
          <div class="stats-trend" :class="item.trend > 0 ? 'up' : 'down'">
            <el-icon><CaretTop v-if="item.trend > 0"/><CaretBottom v-else/></el-icon>
            <span>{{ Math.abs(item.trend) }}%</span>
            <span style="color:rgba(255,255,255,0.3); margin-left:4px;">较上月</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- Main Chart -->
      <el-col :span="16">
        <div class="chart-card">
          <div class="chart-header">
            <h4 style="margin:0; color:white; font-size:16px; font-weight: 800; letter-spacing: -0.01em;">实时打卡人次统计</h4>
            <el-radio-group v-model="checkinTimeRange" size="small" @change="fetchCheckinStats" class="dark-radio">
              <el-radio-button label="7">最近7天</el-radio-button>
              <el-radio-button label="30">最近30天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="checkinChartRef" style="width:100%; height:320px;"></div>
        </div>
      </el-col>

      <!-- Right Side -->
      <el-col :span="8">
        <div class="chart-card">
          <h4 style="margin:0 0 20px; color:white; font-size:16px; font-weight: 800; letter-spacing: -0.01em;">答题正确率分析</h4>
          <div ref="accuracyChartRef" style="width:100%; height:320px;"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { CaretTop, CaretBottom } from '@element-plus/icons-vue'
import request from '@/utils/request'

const statsCards = ref([
  { title: '累计注册居民', value: '0', icon: '👥', color: 'linear-gradient(135deg, #667eea, #764ba2)', trend: 12.5 },
  { title: '全站流转积分', value: '0', icon: '💎', color: 'linear-gradient(135deg, #f6d365, #fda085)', trend: 8.2 },
  { title: '今日新增举报', value: '0', icon: '🚨', color: 'linear-gradient(135deg, #ff0844, #ffb199)', trend: -2.4 },
  { title: '待审核工单', value: '0', icon: '🕒', color: 'linear-gradient(135deg, #43e97b, #38f9d7)', trend: 15.1 }
])

const checkinTimeRange = ref('7')
const checkinChartRef = ref<HTMLElement>()
const accuracyChartRef = ref<HTMLElement>()
let checkinChart: echarts.ECharts | null = null
let accuracyChart: echarts.ECharts | null = null

const fetchStats = async () => {
  try {
    const res = await request.get('/admin/stats/points')
    if (res.data.code === 200) {
      const data = res.data.data
      statsCards.value[0].value = data.totalUsers.toString()
      statsCards.value[1].value = data.totalPoints.toString()
      statsCards.value[0].trend = data.userTrend
      statsCards.value[1].trend = data.pointTrend
    }
    
    // 获取待处理举报数
    const reportRes = await request.get('/report/list', { params: { pageSize: 1, status: 0 } })
    if (reportRes.data.code === 200) {
      statsCards.value[3].value = reportRes.data.data.total.toString()
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchCheckinStats = async () => {
  try {
    const res = await request.get(`/quiz/stats/checkin?days=${checkinTimeRange.value}`)
    if (res.data.code === 200) {
      const data = res.data.data
      const dates = data.dailyStats.map((s: any) => s.date.substring(5))
      const counts = data.dailyStats.map((s: any) => s.count)
      
      if (!checkinChart && checkinChartRef.value) {
        checkinChart = echarts.init(checkinChartRef.value)
      }
      
      checkinChart?.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: { color: 'rgba(255,255,255,0.4)', fontSize: 10 },
          axisLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: 'rgba(255,255,255,0.4)', fontSize: 10 },
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.05)', type: 'dashed' } }
        },
        series: [{
          data: counts,
          type: 'line',
          smooth: true,
          lineStyle: { width: 3, color: '#6366f1' },
          itemStyle: { color: '#6366f1' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(99, 102, 241, 0.3)' },
              { offset: 1, color: 'rgba(99, 102, 241, 0)' }
            ])
          }
        }]
      })
    }
  } catch (error) {
    console.error('加载打卡统计失败:', error)
  }
}

const fetchAccuracyStats = async () => {
  try {
    const res = await request.get('/quiz/stats/accuracy')
    if (res.data.code === 200) {
      const data = res.data.data
      
      if (!accuracyChart && accuracyChartRef.value) {
        accuracyChart = echarts.init(accuracyChartRef.value)
      }
      
      accuracyChart?.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: '5%', left: 'center', textStyle: { color: 'rgba(255,255,255,0.6)', fontSize: 11 } },
        series: [{
          name: '正确率',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#0f172a', borderWidth: 2 },
          label: { show: false },
          emphasis: { label: { show: true, fontSize: '18', fontWeight: 'bold', color: 'white' } },
          data: [
            { value: Math.round(data.overallAccuracy * 100), name: '正确', itemStyle: { color: '#10b981' } },
            { value: Math.round((1 - data.overallAccuracy) * 100), name: '错误', itemStyle: { color: '#ef4444' } }
          ]
        }]
      })
    }
  } catch (error) {
    console.error('加载正确率统计失败:', error)
  }
}

const handleResize = () => {
  checkinChart?.resize()
  accuracyChart?.resize()
}

onMounted(() => {
  fetchStats()
  fetchCheckinStats()
  fetchAccuracyStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.stats-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}
.stats-card:hover { transform: translateY(-4px); }
.stats-title { font-size: 13px; color: rgba(255, 255, 255, 0.4); margin: 0 0 8px; font-weight: 600; letter-spacing: 0.02em; }
.stats-value { font-size: 32px; font-weight: 900; color: white; margin: 0; letter-spacing: -0.02em; }
.stats-icon {
  width: 52px; height: 52px; border-radius: 16px;
  display: flex; align-items: center; justify-content: center; font-size: 24px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}
.stats-trend { display: flex; align-items: center; gap: 4px; margin-top: 16px; font-size: 12px; font-weight: 700; }
.stats-trend.up { color: #4ade80; }
.stats-trend.down { color: #f87171; }

.chart-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 24px;
  height: 100%;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}
.chart-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }

:deep(.dark-radio .el-radio-button__inner) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.5);
}
:deep(.dark-radio .el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}
</style>
