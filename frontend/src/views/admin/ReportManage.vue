<template>
  <div style="display:flex; flex-direction:column; gap:20px; height:100%; position: relative; z-index: 10;">
    <!-- Page Header -->
    <div style="background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 20px; padding: 20px 24px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);">
      <div style="display:flex; align-items:center; gap:16px;">
        <div style="width:48px; height:48px; border-radius:14px; background:linear-gradient(135deg, #f87171, #fb923c); display:flex; align-items:center; justify-content:center; font-size:22px; flex-shrink:0; box-shadow: 0 4px 12px rgba(248, 113, 113, 0.3);">🚨</div>
        <div>
          <h2 style="font-size:18px; font-weight:900; color:white; margin:0; letter-spacing: -0.02em;">居民违规举报审核池</h2>
          <p style="font-size:12px; color:rgba(255,255,255,0.4); margin:4px 0 0;">实时处理社区违规事件，维护良好居住环境</p>
        </div>
      </div>
      <div style="display:flex; gap:10px; align-items:center;">
        <el-select v-model="statusFilter" placeholder="状态筛选" style="width:140px;" clearable @change="loadData">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
      </div>
    </div>

    <!-- Table Card -->
    <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:24px; flex:1; display:flex; flex-direction:column; min-height:0;">
      <el-table :data="tableData" style="width: 100%;" v-loading="loading" class="dark-table">
        <el-table-column prop="id" label="工单编号" width="100" />
        <el-table-column prop="userId" label="举证人ID" width="100" />
        
        <el-table-column label="现场图片" width="100">
          <template #default="scope">
            <el-image 
              style="width:50px; height:50px; border-radius:8px;"
              :src="scope.row.photoUrl"
              :preview-src-list="[scope.row.photoUrl]"
              fit="cover"
              preview-teleported />
          </template>
        </el-table-column>

        <el-table-column prop="locationDetail" label="地址详情" min-width="180" />
        <el-table-column prop="description" label="详细描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="上报时间" width="160">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="当前状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success" size="small">已通过</el-tag>
            <el-tag v-else type="danger" size="small">已驳回</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作处理" width="280">
          <template #default="scope">
            <div style="display:flex; gap:8px;" v-if="scope.row.status === 0">
              <el-button type="success" size="small" @click="handleProcess(scope.row, 1)">通过</el-button>
              <el-button type="danger" size="small" @click="handleProcess(scope.row, 2)">驳回</el-button>
            </div>
            <div style="display:flex; gap:8px;" v-if="scope.row.status === 1">
              <el-button type="primary" size="small" plain @click="handleOpenAdvisory(scope.row)">
                生成劝导工单
              </el-button>
            </div>
            <span v-if="scope.row.processRemark" style="font-size:11px; color:#666;">
              {{ scope.row.processRemark }}
            </span>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top:20px; display:flex; justify-content:flex-end;">
        <el-pagination 
          background 
          layout="prev, pager, next" 
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          class="dark-pagination"
          @current-change="loadData" />
      </div>
    </div>

    <!-- Advisory Order Dialog -->
    <el-dialog v-model="advisoryVisible" title="发起劝导工单" width="500px">
      <el-form :model="advisoryForm" label-position="top">
        <el-form-item label="违规类型">
          <el-select v-model="advisoryForm.violationType" style="width:100%;">
            <el-option label="垃圾占道" value="垃圾占道" />
            <el-option label="违规停车" value="违规停车" />
            <el-option label="噪音扰民" value="噪音扰民" />
            <el-option label="违规宠养" value="违规宠养" />
          </el-select>
        </el-form-item>
        <el-form-item label="劝导内容">
          <el-input v-model="advisoryForm.advisoryContent" type="textarea" :rows="3" placeholder="请输入劝导文字内容..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="advisoryVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdvisory">立即发起</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const statusFilter = ref<number | null>(null)
const tableData = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/report/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        status: statusFilter.value
      }
    })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleProcess = (row: any, targetStatus: number) => {
  const actionName = targetStatus === 1 ? '通过' : '驳回'
  
  ElMessageBox.confirm(`确定要${actionName}该举报吗？`, '处理确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: targetStatus === 1 ? 'success' : 'warning'
  }).then(() => {
    request.post('/report/process', {
      id: row.id,
      status: targetStatus,
      processRemark: actionName === '通过' ? '审核通过并发放积分' : '证据不足驳回'
    }).then((res: any) => {
      if (res.data.code === 200) {
        ElMessage.success('处理完成')
        loadData()
      }
    })
  })
}

// 劝导工单相关
const advisoryVisible = ref(false)
const currentReport = ref<any>(null)
const advisoryForm = ref({
  violationType: '垃圾占道',
  advisoryContent: ''
})

const handleOpenAdvisory = (row: any) => {
  currentReport.value = row
  advisoryForm.value.advisoryContent = `【社区通告】您在 ${row.locationDetail} 处的违规行为已被举报并通过核实，请及时改正。`
  advisoryVisible.value = true
}

const submitAdvisory = async () => {
  try {
    const res = await request.post('/report/generate-advisory', {
      reportId: currentReport.value.id,
      violationType: advisoryForm.value.violationType,
      advisoryContent: advisoryForm.value.advisoryContent,
      targetUserId: currentReport.value.targetUserId // 模拟环境可能为空
    })
    if (res.data.code === 200) {
      ElMessage.success('劝导工单已发起')
      advisoryVisible.value = false
    }
  } catch (error) {
    ElMessage.error('发起失败')
  }
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
