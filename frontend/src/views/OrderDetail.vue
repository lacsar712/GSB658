<template>
  <div style="position:fixed; inset:0; display:flex; flex-direction:column; background:#f0f4ff; overflow:hidden;">
    
    <!-- Header -->
    <div style="flex-shrink:0; background:linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius:0 0 32px 32px; padding:20px 20px 28px; position:relative; overflow:hidden;">
      <div style="position:absolute; width:160px; height:160px; background:rgba(255,255,255,0.08); border-radius:50%; top:-60px; right:-30px;"></div>
      
      <div style="position:relative; z-index:2; display:flex; justify-content:space-between; align-items:center;">
        <button @click="router.back()" style="display:flex; align-items:center; gap:8px; color:rgba(255,255,255,0.9); background:rgba(255,255,255,0.15); border:none; padding:8px 14px; border-radius:12px; cursor:pointer; font-size:14px; font-weight:700; font-family:inherit;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
          订单详情
        </button>
      </div>
    </div>

    <!-- Content -->
    <div style="flex:1; overflow-y:auto; padding:20px 16px;">
      <!-- Loading -->
      <div v-if="loading" style="display:flex; justify-content:center; padding:40px;">
        <span style="font-size:14px; color:#999;">加载中...</span>
      </div>

      <div v-else-if="order" style="display:flex; flex-direction:column; gap:16px;">
        <!-- Status Card -->
        <div style="background:white; border-radius:20px; padding:20px; box-shadow:0 4px 20px rgba(0,0,0,0.08); text-align:center;">
          <div style="font-size:48px; margin-bottom:12px;">{{ getStatusIcon(order.status) }}</div>
          <h3 style="font-size:18px; font-weight:800; color:#1a1a2e; margin:0 0 6px;">{{ getStatusText(order.status) }}</h3>
          <p style="font-size:12px; color:#999; margin:0;">订单号: {{ order.orderNo }}</p>
        </div>

        <!-- Logistics Card -->
        <div v-if="order.logisticsNo" style="background:white; border-radius:20px; padding:20px; box-shadow:0 4px 20px rgba(0,0,0,0.08);">
          <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;">
            <h4 style="font-size:15px; font-weight:800; color:#1a1a2e; margin:0; display:flex; align-items:center; gap:8px;">
              <span>🚚</span> 物流信息
            </h4>
            <button @click="loadLogistics" style="padding:6px 12px; border-radius:8px; background:#f0f0f0; border:none; font-size:12px; color:#666; cursor:pointer; font-family:inherit;">
              刷新
            </button>
          </div>
          
          <div style="background:#f8faff; border-radius:12px; padding:12px; margin-bottom:12px;">
            <div style="font-size:12px; color:#999; margin-bottom:4px;">{{ order.logisticsCompany }}</div>
            <div style="font-size:13px; color:#667eea; font-weight:600;">{{ order.logisticsNo }}</div>
          </div>

          <!-- Logistics Traces -->
          <div v-if="logistics && logistics.traces" style="position:relative; padding-left:24px;">
            <div style="position:absolute; left:7px; top:8px; bottom:8px; width:2px; background:#e5e7eb;"></div>
            
            <div v-for="(trace, index) in logistics.traces" :key="index" 
              style="position:relative; margin-bottom:16px; padding-bottom:16px;"
              :style="index < logistics.traces.length - 1 ? 'border-bottom:1px solid #f0f0f0;' : ''">
              <div :style="index === 0 ? 'position:absolute; left:-20px; top:2px; width:10px; height:10px; border-radius:50%; background:#667eea; border:2px solid white; box-shadow:0 0 0 2px #667eea;' : 'position:absolute; left:-20px; top:2px; width:10px; height:10px; border-radius:50%; background:#e5e7eb;'"></div>
              <div :style="index === 0 ? 'font-size:13px; color:#1a1a2e; font-weight:600; line-height:1.6;' : 'font-size:13px; color:#666; line-height:1.6;'">{{ trace.desc }}</div>
              <div style="font-size:11px; color:#999; margin-top:4px;">{{ trace.time }}</div>
            </div>
          </div>

          <div v-else-if="loadingLogistics" style="text-align:center; padding:20px; color:#999; font-size:13px;">
            加载物流信息中...
          </div>
        </div>

        <!-- Product Card -->
        <div style="background:white; border-radius:20px; padding:20px; box-shadow:0 4px 20px rgba(0,0,0,0.08);">
          <h4 style="font-size:15px; font-weight:800; color:#1a1a2e; margin:0 0 16px;">商品信息</h4>
          
          <div style="display:flex; gap:12px;">
            <div style="width:80px; height:80px; border-radius:16px; background:linear-gradient(135deg, #fff7ed, #ffedd5); display:flex; align-items:center; justify-content:center; font-size:48px; flex-shrink:0;">
              🎁
            </div>
            <div style="flex:1;">
              <h5 style="font-size:14px; font-weight:700; color:#1a1a2e; margin:0 0 8px;">{{ order.productName }}</h5>
              <div style="display:flex; align-items:center; gap:8px;">
                <span style="font-size:20px; font-weight:900; color:#f97316;">{{ order.pointCost }}</span>
                <span style="font-size:12px; color:#999;">积分</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Receiver Card -->
        <div style="background:white; border-radius:20px; padding:20px; box-shadow:0 4px 20px rgba(0,0,0,0.08);">
          <h4 style="font-size:15px; font-weight:800; color:#1a1a2e; margin:0 0 16px;">收货信息</h4>
          
          <div style="display:flex; flex-direction:column; gap:12px;">
            <div style="display:flex; align-items:center; gap:12px;">
              <span style="font-size:12px; color:#999; width:60px;">收货人</span>
              <span style="font-size:13px; color:#1a1a2e; font-weight:600;">{{ order.receiverName || '未填写' }}</span>
            </div>
            <div style="display:flex; align-items:center; gap:12px;">
              <span style="font-size:12px; color:#999; width:60px;">联系电话</span>
              <span style="font-size:13px; color:#1a1a2e;">{{ order.receiverPhone || '未填写' }}</span>
            </div>
            <div style="display:flex; gap:12px;">
              <span style="font-size:12px; color:#999; width:60px; flex-shrink:0;">收货地址</span>
              <span style="font-size:13px; color:#1a1a2e; line-height:1.6;">{{ order.receiverAddress || '未填写' }}</span>
            </div>
          </div>
        </div>

        <!-- Order Info Card -->
        <div style="background:white; border-radius:20px; padding:20px; box-shadow:0 4px 20px rgba(0,0,0,0.08);">
          <h4 style="font-size:15px; font-weight:800; color:#1a1a2e; margin:0 0 16px;">订单信息</h4>
          
          <div style="display:flex; flex-direction:column; gap:12px;">
            <div style="display:flex; justify-content:space-between;">
              <span style="font-size:12px; color:#999;">订单编号</span>
              <span style="font-size:12px; color:#1a1a2e; font-weight:600;">{{ order.orderNo }}</span>
            </div>
            <div style="display:flex; justify-content:space-between;">
              <span style="font-size:12px; color:#999;">下单时间</span>
              <span style="font-size:12px; color:#1a1a2e;">{{ formatTime(order.createTime) }}</span>
            </div>
            <div style="display:flex; justify-content:space-between;">
              <span style="font-size:12px; color:#999;">更新时间</span>
              <span style="font-size:12px; color:#1a1a2e;">{{ formatTime(order.updateTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const loadingLogistics = ref(false)
const order = ref<any>(null)
const logistics = ref<any>(null)

onMounted(async () => {
  await loadOrder()
  if (order.value?.logisticsNo) {
    await loadLogistics()
  }
})

const loadOrder = async () => {
  try {
    const orderId = route.params.id
    const response = await request.get(`/point/orders/${orderId}`)
    if (response.data.code === 200) {
      order.value = response.data.data
    }
  } catch (error: any) {
    console.error('加载订单失败:', error)
    ElMessage.error(error.response?.data?.message || '加载订单失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const loadLogistics = async () => {
  if (!order.value?.logisticsNo) return
  
  loadingLogistics.value = true
  try {
    const response = await request.get(`/point/logistics/${order.value.id}`)
    if (response.data.code === 200) {
      logistics.value = response.data.data
    }
  } catch (error) {
    console.error('加载物流信息失败:', error)
  } finally {
    loadingLogistics.value = false
  }
}

const getStatusText = (status: number) => {
  const map: any = { 0: '待发货', 1: '运输中', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

const getStatusIcon = (status: number) => {
  const map: any = { 0: '📦', 1: '🚚', 2: '✅', 3: '❌' }
  return map[status] || '📦'
}

const formatTime = (time: string) => {
  return time ? time.replace('T', ' ').substring(0, 19) : ''
}
</script>
