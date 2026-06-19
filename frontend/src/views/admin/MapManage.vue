<template>
  <div style="display:flex; flex-direction:column; gap:20px; height:100%; position: relative; z-index: 10;">

    <!-- Header -->
    <div style="background: rgba(255, 255, 255, 0.03); backdrop-filter: blur(10px); border: 1px solid rgba(255, 255, 255, 0.08); border-radius: 20px; padding: 18px 24px; display: flex; align-items: center; justify-content: space-between; flex-shrink: 0; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);">
      <div style="display:flex; align-items:center; gap:14px;">
        <div style="width:46px; height:46px; border-radius:14px; background:linear-gradient(135deg, #60a5fa, #2dd4bf); display:flex; align-items:center; justify-content:center; font-size:20px; flex-shrink:0; box-shadow: 0 4px 12px rgba(96, 165, 250, 0.3);">🗺️</div>
        <div>
          <h2 style="font-size:17px; font-weight:900; color:white; margin:0; letter-spacing: -0.01em;">虚拟设备大屏雷达</h2>
          <p style="font-size:12px; color:rgba(255,255,255,0.4); margin:3px 0 0;">实时监控社区智能垃圾桶、回收箱、驿站等设备状态</p>
        </div>
      </div>
      <!-- Stats badges -->
      <div style="display:flex; gap:10px;">
        <div style="display:flex; align-items:center; gap:6px; padding:6px 14px; background:rgba(74,222,128,0.12); border:1px solid rgba(74,222,128,0.25); border-radius:20px;">
          <span style="width:8px; height:8px; border-radius:50%; background:#4ade80; display:inline-block;"></span>
          <span style="font-size:12px; color:#4ade80; font-weight:700;">在线正常 120</span>
        </div>
        <div style="display:flex; align-items:center; gap:6px; padding:6px 14px; background:rgba(251,191,36,0.12); border:1px solid rgba(251,191,36,0.25); border-radius:20px;">
          <span style="width:8px; height:8px; border-radius:50%; background:#fbbf24; display:inline-block;"></span>
          <span style="font-size:12px; color:#fbbf24; font-weight:700;">容量预警 5</span>
        </div>
        <div style="display:flex; align-items:center; gap:6px; padding:6px 14px; background:rgba(248,113,113,0.12); border:1px solid rgba(248,113,113,0.25); border-radius:20px;">
          <span style="width:8px; height:8px; border-radius:50%; background:#f87171; display:inline-block;"></span>
          <span style="font-size:12px; color:#f87171; font-weight:700;">满溢故障 2</span>
        </div>
      </div>
    </div>

    <!-- Main: Map + Device List -->
    <div style="display:grid; grid-template-columns:1fr 300px; gap:16px; flex:1; min-height:0;">

      <!-- Map Area -->
      <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; overflow:hidden; position:relative; display:flex; align-items:center; justify-content:center;">
        <!-- Simulated map grid background -->
        <div style="position:absolute; inset:0; background-image: linear-gradient(rgba(79,172,254,0.05) 1px, transparent 1px), linear-gradient(90deg, rgba(79,172,254,0.05) 1px, transparent 1px); background-size: 40px 40px;"></div>
        <!-- Device pins on map -->
        <div v-for="dev in devices" :key="dev.id"
          :style="`position:absolute; left:${dev.mapX}%; top:${dev.mapY}%; transform:translate(-50%,-100%); cursor:pointer; z-index:5;`"
          @click="selectDevice(dev)">
          <div :style="`width:36px; height:36px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:16px; border:3px solid ${getStatusColor(dev.status)}; background:rgba(15,23,42,0.85); box-shadow: 0 0 12px ${getStatusColor(dev.status)}60; transition: transform 0.2s;`"
            @mouseenter="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.transform='scale(1.2)'"
            @mouseleave="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.transform='scale(1)'">
            🗑️
          </div>
          <div style="width:2px; height:12px; background:rgba(255,255,255,0.3); margin:0 auto;"></div>
        </div>
        <!-- Map watermark -->
        <div style="z-index:1; text-align:center; color:rgba(255,255,255,0.15); pointer-events:none;">
          <div style="font-size:48px; margin-bottom:8px; opacity:0.4;">🏙️</div>
          <p style="font-size:13px; font-weight:600;">社区设备分布图（模拟）</p>
          <p style="font-size:11px; margin-top:4px; opacity:0.6;">点击左侧设备卡片聚焦位置</p>
        </div>
        <!-- Selected device popup -->
        <div v-if="selectedDev" style="position:absolute; bottom:20px; left:50%; transform:translateX(-50%); background:rgba(15,23,42,0.9); border:1px solid rgba(79,172,254,0.3); border-radius:14px; padding:14px 20px; backdrop-filter:blur(10px); z-index:10; min-width:260px; text-align:center;">
          <p style="font-size:14px; font-weight:700; color:white; margin:0 0 4px;">{{ selectedDev.name }}</p>
          <p style="font-size:11px; color:rgba(255,255,255,0.5); margin:0;">{{ selectedDev.deviceNo }} · {{ selectedDev.lng }}, {{ selectedDev.lat }}</p>
          <p :style="`font-size:12px; font-weight:700; margin:8px 0 0; color:${getStatusColor(selectedDev.status)}`">● {{ getStatusText(selectedDev.status) }}</p>
        </div>
      </div>

      <!-- Device List -->
      <div style="background:rgba(255,255,255,0.04); border:1px solid rgba(255,255,255,0.08); border-radius:20px; padding:18px; display:flex; flex-direction:column; gap:12px; overflow-y:auto;">
        <div style="display:flex; align-items:center; justify-content:space-between; flex-shrink:0;">
          <h3 style="font-size:13px; font-weight:800; color:white; margin:0;">设备列表</h3>
          <span style="font-size:11px; color:rgba(255,255,255,0.35);">共 {{ devices.length }} 台</span>
        </div>
        <!-- Search -->
        <div style="position:relative; flex-shrink:0;">
          <span style="position:absolute; left:10px; top:50%; transform:translateY(-50%); font-size:13px; color:rgba(255,255,255,0.3);">🔍</span>
          <input v-model="searchDev" placeholder="搜索设备名称 / 编号"
            style="width:100%; background:rgba(255,255,255,0.06); border:1px solid rgba(255,255,255,0.1); border-radius:10px; padding:8px 10px 8px 32px; color:white; font-size:12px; outline:none; font-family:inherit; box-sizing:border-box;" />
        </div>
        <!-- Device cards -->
        <div v-for="dev in filteredDevices" :key="dev.id"
          @click="selectDevice(dev)"
          :style="`background:${selectedDev?.id === dev.id ? 'rgba(79,172,254,0.1)' : 'rgba(255,255,255,0.03)'}; border:1px solid ${selectedDev?.id === dev.id ? 'rgba(79,172,254,0.35)' : 'rgba(255,255,255,0.07)'}; border-radius:14px; padding:12px 14px; cursor:pointer; transition:all 0.2s;`"
          @mouseenter="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.background='rgba(79,172,254,0.08)'"
          @mouseleave="(e: MouseEvent) => (e.currentTarget as HTMLElement).style.background = selectedDev?.id === dev.id ? 'rgba(79,172,254,0.1)' : 'rgba(255,255,255,0.03)'">
          <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:6px;">
            <span style="font-size:13px; font-weight:700; color:white;">{{ dev.name }}</span>
            <span :style="`font-size:10px; font-weight:700; padding:2px 8px; border-radius:20px; background:${getStatusBg(dev.status)}; color:${getStatusColor(dev.status)};`">
              {{ getStatusText(dev.status) }}
            </span>
          </div>
          <p style="font-size:11px; color:rgba(255,255,255,0.35); margin:0; font-family:monospace;">{{ dev.deviceNo }}</p>
          <p style="font-size:10px; color:rgba(255,255,255,0.25); margin:3px 0 0;">📍 {{ dev.lng }}, {{ dev.lat }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Device { id: number; deviceNo: string; name: string; lng: number; lat: number; status: number; mapX: number; mapY: number }

const searchDev = ref('')
const selectedDev = ref<Device | null>(null)

const devices = ref<Device[]>([
  { id: 1, deviceNo: 'BIN-001', name: '东一门智能感应垃圾桶', lng: 121.473701, lat: 31.230416, status: 0, mapX: 35, mapY: 40 },
  { id: 2, deviceNo: 'BIN-002', name: '西二门生活垃圾集中点', lng: 121.475112, lat: 31.231200, status: 1, mapX: 62, mapY: 30 },
  { id: 3, deviceNo: 'BIN-003', name: '中央广场快递包装回收箱', lng: 121.471239, lat: 31.229155, status: 2, mapX: 50, mapY: 60 },
])

const filteredDevices = computed(() => {
  const k = searchDev.value.trim()
  return k ? devices.value.filter((d: Device) => d.name.includes(k) || d.deviceNo.includes(k)) : devices.value
})

const getStatusColor = (s: number) => ['#4ade80', '#fbbf24', '#f87171'][s] ?? '#9ca3af'
const getStatusBg = (s: number) => ['rgba(74,222,128,0.12)', 'rgba(251,191,36,0.12)', 'rgba(248,113,113,0.12)'][s] ?? 'rgba(156,163,175,0.1)'
const getStatusText = (s: number) => ['工作正常', '容量预警', '满溢待清'][s]

const selectDevice = (dev: Device) => {
  selectedDev.value = selectedDev.value?.id === dev.id ? null : dev
}
</script>
