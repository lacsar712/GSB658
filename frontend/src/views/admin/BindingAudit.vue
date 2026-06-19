<template>
  <div style="display:flex; flex-direction:column; gap:20px; height:100%; position: relative; z-index: 10;">
    <!-- Page Header -->
    <div style="background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 20px; padding: 20px 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);">
      <div style="display:flex; align-items:center; gap:16px;">
        <div style="width:48px; height:48px; border-radius:14px; background:linear-gradient(135deg, #10b981, #3b82f6); display:flex; align-items:center; justify-content:center; font-size:22px; flex-shrink:0; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);">🆔</div>
        <div>
          <h2 style="font-size:18px; font-weight:900; color:white; margin:0; letter-spacing: -0.02em;">居民社区绑定审核</h2>
          <p style="font-size:12px; color:rgba(255,255,255,0.4); margin:4px 0 0;">人工核实居民身份资料，确保社区服务精准投放</p>
        </div>
      </div>
    </div>

    <!-- Table Card -->
    <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:24px; flex:1; display:flex; flex-direction:column; min-height:0;">
      <el-table :data="tableData" style="width: 100%;" v-loading="loading" class="dark-table">
        <el-table-column prop="id" label="申请编号" width="90" />
        <el-table-column prop="userId" label="居民ID" width="90" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="communityName" label="申请社区" width="180" />
        <el-table-column prop="addressDetail" label="详细住址" min-width="200" show-overflow-tooltip />
        
        <el-table-column label="证明材料" width="140">
          <template #default="scope">
            <el-image 
              v-if="scope.row.provePhoto"
              style="width:56px; height:56px; border-radius:10px; border:1px solid rgba(255,255,255,0.1); display: block;"
              :src="scope.row.provePhoto"
              :preview-src-list="[scope.row.provePhoto]"
              fit="cover"
              preview-teleported />
            <span v-else style="color:rgba(255,255,255,0.2); font-size:12px;">未上传</span>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="申请时间" width="160">
          <template #default="scope">
            <span style="color:rgba(255,255,255,0.5); font-size:12px;">{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220">
          <template #default="scope">
            <div style="display:flex; gap:8px;">
              <el-button type="success" size="small" @click="handleAudit(scope.row, 1)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="handleAudit(scope.row, 2)">
                驳回
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/binding/admin/pending')
    if (res.data.code === 200) {
      tableData.value = res.data.data
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAudit = (row: any, status: number) => {
  const statusName = status === 1 ? '通过' : '驳回'
  
  ElMessageBox.prompt(`请输入${statusName}理由（可选）`, `${statusName}确认`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(({ value }: { value: string }) => {
    request.post('/binding/admin/audit', {
      id: row.id,
      status: status,
      auditRemark: value || (status === 1 ? '资料完整，准予通过' : '资料不全，请重新上传')
    }).then((res: any) => {
      if (res.data.code === 200) {
        ElMessage.success('审核处理成功')
        loadData()
      }
    })
  }).catch(() => {})
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
