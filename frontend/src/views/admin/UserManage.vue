<template>
  <div style="display:flex; flex-direction:column; gap:20px; height:100%; position: relative; z-index: 10;">
    <!-- Page Header -->
    <div style="background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 20px; padding: 20px 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);">
      <div style="display:flex; align-items:center; gap:16px;">
        <div style="width:48px; height:48px; border-radius:14px; background:linear-gradient(135deg, #6366f1, #8b5cf6); display:flex; align-items:center; justify-content:center; font-size:22px; flex-shrink:0; box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);">👥</div>
        <div>
          <h2 style="font-size:18px; font-weight:900; color:white; margin:0; letter-spacing: -0.02em;">社区居民档案管理</h2>
          <p style="font-size:12px; color:rgba(255,255,255,0.4); margin:4px 0 0;">统一管理社区居民信息、积分余额及账号状态</p>
        </div>
      </div>
      <div style="display:flex; gap:10px; align-items:center;">
        <el-input 
          v-model="searchKey" 
          placeholder="输入手机号/昵称检索" 
          :prefix-icon="Search" 
          style="width:240px;" 
          clearable 
          @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询数据</el-button>
        <el-button type="success" @click="handleExportExcel">
          <el-icon><Download /></el-icon> 导出Excel
        </el-button>
      </div>
    </div>

    <!-- Table Card -->
    <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:24px; flex:1; display:flex; flex-direction:column; min-height:0;">
      <el-table :data="tableData" style="width: 100%;" v-loading="loading" class="dark-table">
        <el-table-column prop="id" label="居民ID" width="90" />
        
        <el-table-column label="昵称" width="160">
          <template #default="scope">
            <div style="display:flex; align-items:center; gap:10px;">
              <div style="width:32px; height:32px; border-radius:10px; background:linear-gradient(135deg, #667eea, #764ba2); display:flex; align-items:center; justify-content:center; color:white; font-weight:900; font-size:14px; flex-shrink:0;">
                {{ scope.row.nickname ? scope.row.nickname.charAt(0) : 'U' }}
              </div>
              <span style="font-weight:700; color:rgba(255,255,255,0.9); font-size:14px;">{{ scope.row.nickname }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="phone" label="联系方式" width="130">
          <template #default="scope">
            <span style="color:rgba(255,255,255,0.6); font-size:13px;">{{ scope.row.phone }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="绑定社区" min-width="200">
          <template #default="scope">
            <div style="display:flex; align-items:center; gap:6px; color:rgba(255,255,255,0.6); font-size:13px;">
              <el-icon style="color:#60a5fa;"><House /></el-icon>
              <span>{{ scope.row.communityId ? '碧桂园凤凰城' : '尚未绑定' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="积分余额" width="130" sortable>
          <template #default="scope">
            <div style="display:inline-flex; align-items:center; gap:4px; padding:4px 12px; background:rgba(251,191,36,0.15); border:1px solid rgba(251,191,36,0.3); border-radius:10px;">
              <span style="font-weight:900; color:#fbbf24; font-size:15px;">{{ scope.row.pointBalance }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="连续打卡" width="110">
          <template #default="scope">
            <div style="display:flex; align-items:center; gap:4px;">
              <span style="font-size:16px;">🔥</span>
              <span style="color:#f87171; font-weight:700; font-size:14px;">{{ scope.row.consecutiveDays || 0 }}</span>
              <span style="color:rgba(255,255,255,0.4); font-size:11px;">天</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="注册时间" width="160">
          <template #default="scope">
            <span style="color:rgba(255,255,255,0.5); font-size:12px;">{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="账号状态" width="110">
          <template #default="scope">
            <el-switch 
              v-model="scope.row.status" 
              :active-value="1" 
              :inactive-value="0" 
              active-color="#10B981" 
              inactive-color="#EF4444"
              inline-prompt 
              active-text="正常" 
              inactive-text="封禁"
              @change="(val: number | string | boolean) => handleStatusChange(val, scope.row)" />
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top:20px; display:flex; justify-content:space-between; align-items:center;">
        <span style="color:rgba(255,255,255,0.5); font-size:13px;">
          共检索到 <span style="color:#667eea; font-weight:900; margin:0 4px;">{{ totalCount }}</span> 条居民数据
        </span>
        <el-pagination 
          background 
          layout="prev, pager, next" 
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="totalCount"
          class="dark-pagination"
          @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Search, House } from '@element-plus/icons-vue'
import request from '@/utils/request'

const searchKey = ref('')
const loading = ref(false)
const tableData = ref<any[]>([])
const totalCount = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/admin/users', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: searchKey.value
      }
    })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      totalCount.value = res.data.data.total
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleStatusChange = (val: any, row: any) => {
  // 实际项目中应调用API
  const action = val === 1 ? '解封' : '封禁'
  ElMessage({ type: val === 1 ? 'success' : 'warning', message: `操作成功: 已${action}用户 ${row.nickname}` })
}

const handleExportExcel = () => {
  ElMessage.info('正在生成报表...')
}

const formatTime = (time: string) => {
  return time ? time.substring(0, 16).replace('T', ' ') : ''
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
:deep(.dark-table) { background: transparent !important; }
:deep(.dark-table .el-table__header-wrapper) { background: rgba(255,255,255,0.03); }
:deep(.dark-table th.el-table__cell) { background: rgba(255,255,255,0.03) !important; color: rgba(255,255,255,0.6) !important; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,0.08) !important; }
:deep(.dark-table td.el-table__cell) { background: transparent !important; border-bottom: 1px solid rgba(255,255,255,0.05) !important; color: rgba(255,255,255,0.8); }
</style>
