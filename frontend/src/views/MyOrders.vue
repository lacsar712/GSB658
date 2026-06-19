<template>
  <div style="position:fixed; inset:0; display:flex; flex-direction:column; background:#f0f4ff; overflow:hidden;">
    
    <!-- Header -->
    <div style="flex-shrink:0; background:linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius:0 0 32px 32px; padding:20px 20px 28px; position:relative; overflow:hidden;">
      <div style="position:absolute; width:160px; height:160px; background:rgba(255,255,255,0.08); border-radius:50%; top:-60px; right:-30px;"></div>
      
      <div style="position:relative; z-index:2; display:flex; justify-content:space-between; align-items:center;">
        <button @click="router.back()" style="display:flex; align-items:center; gap:8px; color:rgba(255,255,255,0.9); background:rgba(255,255,255,0.15); border:none; padding:8px 14px; border-radius:12px; cursor:pointer; font-size:14px; font-weight:700; font-family:inherit;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
          我的订单
        </button>
      </div>
    </div>

    <!-- Orders List -->
    <div style="flex:1; overflow-y:auto; padding:20px 16px;">
      <!-- Loading -->
      <div v-if="loading" style="display:flex; justify-content:center; padding:40px;">
        <span style="font-size:14px; color:#999;">加载中...</span>
      </div>

      <!-- Empty -->
      <div v-else-if="!orders.length" style="text-align:center; padding:60px 20px;">
        <div style="font-size:64px; margin-bottom:16px;">📦</div>
        <p style="font-size:15px; color:#999; margin:0;">暂无订单记录</p>
        <button @click="router.push('/point-mall')" style="margin-top:20px; padding:10px 24px; border-radius:12px; background:linear-gradient(135deg, #667eea, #764ba2); color:white; border:none; font-size:14px; font-weight:700; cursor:pointer;">
          去兑换商品
        </button>
      </div>

      <!-- Order Cards -->
      <div v-else style="display:flex; flex-direction:column; gap:16px;">
        <div v-for="order in orders" :key="order.id" 
          @click="viewDetail(order)"
          style="background:white; border-radius:20px; padding:16px; box-shadow:0 4px 20px rgba(0,0,0,0.08); cursor:pointer; transition:transform 0.2s;"
          @mouseenter="e => e.currentTarget.style.transform='translateY(-2px)'"
          @mouseleave="e => e.currentTarget.style.transform=''">
          
          <!-- Order Header -->
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px; padding-bottom:12px; border-bottom:1px solid #f0f0f0;">
            <span style="font-size:12px; color:#999;">订单号: {{ order.orderNo }}</span>
            <span :style="getStatusStyle(order.status)">{{ getStatusText(order.status) }}</span>
          </div>

          <!-- Product Info -->
          <div style="display:flex; gap:12px; margin-bottom:12px;">
            <div style="width:60px; height:60px; border-radius:12px; background:linear-gradient(135deg, #fff7ed, #ffedd5); display:flex; align-items:center; justify-content:center; font-size:32px; flex-shrink:0;">
              🎁
            </div>
            <div style="flex:1; min-width:0;">
              <h4 style="font-size:14px; font-weight:700; color:#1a1a2e; margin:0 0 4px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">{{ order.productName }}</h4>
              <p style="font-size:12px; color:#999; margin:0;">{{ formatTime(order.createTime) }}</p>
            </div>
            <div style="text-align:right;">
              <div style="font-size:18px; font-weight:900; color:#f97316;">{{ order.pointCost }}</div>
              <div style="font-size:10px; color:#999;">积分</div>
            </div>
          </div>

          <!-- Logistics Info -->
          <div v-if="order.logisticsNo" style="background:#f8faff; border-radius:12px; padding:10px 12px; display:flex; align-items:center; gap:8px;">
            <span style="font-size:16px;">🚚</span>
            <div style="flex:1; min-width:0;">
              <div style="font-size:11px; color:#999;">{{ order.logisticsCompany }}</div>
              <div style="font-size:12px; color:#667eea; font-weight:600; overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">{{ order.logisticsNo }}</div>
            </div>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#999" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(true)
const orders = ref<any[]>([])

onMounted(async () => {
  await loadOrders()
})

const loadOrders = async () => {
  try {
    const res = await request.get('/point/orders')
    if (res.data.code === 200) {
      orders.value = res.data.data
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (order: any) => {
  router.push(`/orders/${order.id}`)
}

const getStatusText = (status: number) => {
  const map: any = { 0: '待发货', 1: '已发货', 2: '已完成', 3: '已取消' }
  return map[status] || '未知'
}

const getStatusStyle = (status: number) => {
  const styles: any = {
    0: 'font-size:11px; padding:3px 10px; border-radius:12px; background:#fff7ed; color:#f97316; font-weight:600;',
    1: 'font-size:11px; padding:3px 10px; border-radius:12px; background:#eff6ff; color:#3b82f6; font-weight:600;',
    2: 'font-size:11px; padding:3px 10px; border-radius:12px; background:#f0fdf4; color:#22c55e; font-weight:600;',
    3: 'font-size:11px; padding:3px 10px; border-radius:12px; background:#f5f5f5; color:#999; font-weight:600;'
  }
  return styles[status] || styles[0]
}

const formatTime = (time: string) => {
  return time ? time.substring(0, 16).replace('T', ' ') : ''
}
</script>
