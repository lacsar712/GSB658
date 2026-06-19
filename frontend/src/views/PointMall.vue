<template>
  <div style="position:fixed; inset:0; display:flex; flex-direction:column; background:#f0f4ff; overflow:hidden;">
    
    <!-- Header -->
    <div style="flex-shrink:0; background:linear-gradient(135deg, #f97316 0%, #ea580c 100%); border-radius:0 0 32px 32px; padding:20px 20px 28px; position:relative; overflow:hidden;">
      <!-- Decorative -->
      <div style="position:absolute; width:160px; height:160px; background:rgba(255,255,255,0.08); border-radius:50%; top:-60px; right:-30px;"></div>
      <div style="position:absolute; width:80px; height:80px; background:rgba(255,255,255,0.06); border-radius:50%; bottom:-20px; left:30px;"></div>

      <!-- Top row -->
      <div style="position:relative; z-index:2; display:flex; justify-content:space-between; align-items:center;">
        <button @click="router.back()" style="display:flex; align-items:center; gap:8px; color:rgba(255,255,255,0.9); background:rgba(255,255,255,0.15); border:none; padding:8px 14px; border-radius:12px; cursor:pointer; font-size:14px; font-weight:700; font-family:inherit;">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M19 12H5M12 5l-7 7 7 7"/></svg>
          积分商城
        </button>
        <div style="display:flex; align-items:center; gap:6px; padding:8px 16px; border-radius:20px; background:rgba(255,255,255,0.18); backdrop-filter:blur(10px);">
          <span style="font-size:18px;">🪙</span>
          <span style="color:white; font-weight:900; font-size:18px;">{{ currentPoints }}</span>
          <span style="color:rgba(255,255,255,0.7); font-size:11px;">积分</span>
        </div>
      </div>

      <!-- Banner -->
      <div style="position:relative; z-index:2; margin-top:16px; padding:16px 18px; border-radius:18px; background:rgba(255,255,255,0.15); backdrop-filter:blur(20px); border:1px solid rgba(255,255,255,0.28);">
        <p style="color:rgba(255,255,255,0.65); font-size:10px; font-weight:700; letter-spacing:0.1em; text-transform:uppercase; margin:0;">🔥 本周特惠</p>
        <h2 style="color:white; font-size:22px; font-weight:900; margin:4px 0 2px;">周末狂欢兑 🎁</h2>
        <p style="color:rgba(255,255,255,0.8); font-size:12px; margin:0;">生活用品全场8折起，限量抢购</p>
      </div>
    </div>

    <!-- Products Scrollable Area -->
    <div style="flex:1; overflow-y:auto; padding:20px 16px 16px;">
      <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;">
        <div style="display:flex; align-items:center; gap:8px;">
          <div style="width:4px; height:20px; border-radius:4px; background:linear-gradient(to bottom, #f97316, #ea580c);"></div>
          <h3 style="font-size:17px; font-weight:900; color:#1a1a2e; margin:0;">热门商品</h3>
        </div>
        <span style="font-size:11px; color:#aaa;">共 {{ products.length }} 件</span>
      </div>

      <!-- 2-column Product Grid -->
      <div style="display:grid; grid-template-columns:1fr 1fr; gap:14px;">
        <div v-for="item in products" :key="item.id"
          @click="handleExchange(item)"
          style="background:white; border-radius:20px; overflow:hidden; box-shadow:0 4px 20px rgba(0,0,0,0.08); cursor:pointer; transition:transform 0.25s, box-shadow 0.25s;"
          @mouseenter="e => { e.currentTarget.style.transform='translateY(-5px)'; e.currentTarget.style.boxShadow='0 14px 40px rgba(0,0,0,0.14)' }"
          @mouseleave="e => { e.currentTarget.style.transform=''; e.currentTarget.style.boxShadow='0 4px 20px rgba(0,0,0,0.08)' }">
          
          <!-- Image Area -->
          <div style="height:120px; background:linear-gradient(135deg, #fff7ed, #ffedd5); display:flex; align-items:center; justify-content:center; position:relative;">
            <span style="font-size:52px; filter:drop-shadow(0 4px 8px rgba(0,0,0,0.12));">{{ item.emoji }}</span>
            <!-- Low stock badge -->
            <div v-if="item.stock <= 5 && item.stock > 0" style="position:absolute; top:8px; left:8px; background:linear-gradient(135deg, #f87171, #ef4444); color:white; font-size:10px; font-weight:700; padding:2px 8px; border-radius:20px;">仅剩 {{ item.stock }} 件</div>
            <!-- Sold out overlay -->
            <div v-if="item.stock === 0" style="position:absolute; inset:0; background:rgba(0,0,0,0.45); display:flex; align-items:center; justify-content:center; color:white; font-weight:900; font-size:15px; letter-spacing:3px;">已兑完</div>
          </div>

          <!-- Info Area -->
          <div style="padding:12px 12px 14px;">
            <h4 style="font-size:13px; font-weight:700; color:#1a1a2e; margin:0 0 4px; line-height:1.4; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden;">{{ item.name }}</h4>
            <p style="font-size:11px; color:#bbb; margin:0 0 10px;">库存: {{ item.stock }} 件</p>
            <div style="display:flex; align-items:center; justify-content:space-between;">
              <div>
                <span style="font-size:20px; font-weight:900; color:#f97316;">{{ item.price }}</span>
                <span style="font-size:10px; color:#f97316; margin-left:2px;">分</span>
              </div>
              <div :style="item.stock === 0 ? 'opacity:0.3;' : ''" style="background:linear-gradient(135deg, #f97316, #ea580c); color:white; font-size:12px; font-weight:700; padding:6px 14px; border-radius:20px; box-shadow:0 4px 12px rgba(249,115,22,0.4);">
                兑换
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const currentPoints = ref(0)
const products = ref([
  { id: 1, name: '心相印抽纸经典版 1 提（社区专享）', price: 50, stock: 100, emoji: '🧻' },
  { id: 2, name: '金龙鱼葵花籽油 5L 整箱装', price: 500, stock: 3, emoji: '🛢️' },
  { id: 3, name: '社区上门空调清洗服务券', price: 200, stock: 50, emoji: '❄️' },
  { id: 4, name: '居委会多功能环保手提袋', price: 10, stock: 0, emoji: '👜' },
  { id: 5, name: '爱奇艺会员月卡', price: 300, stock: 20, emoji: '📺' },
  { id: 6, name: '社区菜园有机蔬菜礼盒', price: 150, stock: 15, emoji: '🥦' },
])

// 加载用户积分余额
const loadPointBalance = async () => {
  try {
    const response = await request.get('/point/balance')
    if (response.data.code === 200) {
      currentPoints.value = response.data.data.balance
    }
  } catch (error) {
    console.error('获取积分余额失败:', error)
  }
}

onMounted(() => {
  loadPointBalance()
})

const handleExchange = async (item: any) => {
  if (item.stock === 0) {
    ElMessage.warning('该商品已兑完')
    return
  }
  if (currentPoints.value < item.price) {
    ElMessage.warning(`积分不足，还差 ${item.price - currentPoints.value} 分`)
    return
  }
  
  try {
    await ElMessageBox.confirm(`消耗 ${item.price} 积分兑换「${item.name}」？`, '确认兑换', {
      confirmButtonText: '立即兑换 🎉',
      cancelButtonText: '再想想',
      type: 'success',
    })
    
    // 调用后端API进行兑换
    const response = await request.post('/point/exchange', {
      productId: item.id,
      pointCost: item.price,
      productName: item.name
    })
    
    if (response.data.code === 200) {
      ElMessage.success('🎁 ' + response.data.data.message)
      // 更新本地积分显示
      currentPoints.value = response.data.data.newBalance
      // 减少库存
      item.stock--
    } else {
      ElMessage.error(response.data.message || '兑换失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('兑换失败:', error)
      ElMessage.error(error.response?.data?.message || '兑换失败，请稍后重试')
    }
  }
}
</script>
