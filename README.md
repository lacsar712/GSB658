# 社区智联管理系统

> 最后更新：2026-03-18  
> 面向新手：按本文档步骤执行，可在本机完整启动并体验居民端 + 管理端。

## 🛠 技术栈
- Frontend: Vue 3 + Vite + TypeScript + TailwindCSS + Element Plus
- Backend: Java 17 + Spring Boot 2.7 + MyBatis-Plus + JWT
- DB: MySQL 5.7
- Cache: Redis 7
- Deploy: Docker Compose + Nginx

## 📦 项目结构
```text
.
├── frontend/                         # 前端（居民端 + 管理端）
│   ├── src/
│   │   ├── views/                    # 页面（居民端 + 管理端）
│   │   ├── router/                   # 前端路由与权限控制
│   │   ├── utils/request.ts          # Axios 封装（JWT 自动注入）
│   │   ├── main.ts                   # 前端入口
│   │   └── App.vue                   # 根组件
│   ├── vite.config.ts                # Vite 配置（含 /api 代理）
│   ├── tailwind.config.js            # Tailwind 配置
│   ├── package.json                  # 前端依赖与脚本
│   └── Dockerfile                    # 前端镜像构建（Nginx 托管）
├── backend/                          # 后端 API 服务
│   ├── src/main/java/com/community/system/
│   │   ├── controller/               # 接口层（auth/report/quiz/point/admin...）
│   │   ├── service/                  # 业务接口
│   │   ├── service/impl/             # 业务实现
│   │   ├── mapper/                   # MyBatis-Plus Mapper
│   │   ├── entity/                   # 实体类
│   │   ├── config/                   # JWT拦截器/全局配置
│   │   └── CommunityApplication.java # Spring Boot 启动类
│   ├── src/main/resources/
│   │   └── application.yml           # 数据源/Redis/JWT/第三方配置
│   ├── pom.xml                       # 后端 Maven 依赖
│   └── Dockerfile                    # 后端镜像构建
├── database/
│   ├── init.sql                      # 建表 + 初始化种子数据
│   └── migration_*.sql               # 迭代迁移脚本
├── docs/                             # 需求/架构/设计文档
├── scripts/
│   └── package_release.sh            # 打包发布脚本
├── docker-compose.yml                # 一键启动编排（前后端+MySQL+Redis）
└── README.md
```

## 🧭 按功能看代码（新手阅读顺序）
建议按下面顺序阅读，能最快建立“页面 -> 接口 -> 业务 -> 数据”的完整链路。

1. 先看项目怎么启动（先有全局认知）
   - `docker-compose.yml`
   - `frontend/Dockerfile`
   - `backend/Dockerfile`
   - 目标：知道四个服务怎么连起来（frontend/backend/mysql/redis）

2. 看前端入口与路由（先认识页面全貌）
   - `frontend/src/main.ts`
   - `frontend/src/router/index.ts`
   - 目标：知道有哪些页面、管理员与居民端如何分流

3. 看登录鉴权主链路（最关键）
   - 前端：`frontend/src/views/Login.vue`
   - 前端请求封装：`frontend/src/utils/request.ts`
   - 后端登录接口：`backend/src/main/java/com/community/system/controller/AuthController.java`
   - 后端登录逻辑：`backend/src/main/java/com/community/system/service/impl/UserServiceImpl.java`
   - JWT 拦截：`backend/src/main/java/com/community/system/config/JwtInterceptor.java`
   - 目标：搞清楚 token 如何签发、携带、校验

4. 看居民首页主流程（答题 + 举报 + 社区绑定）
   - 前端主页：`frontend/src/views/Home.vue`
   - 后端答题接口：`backend/src/main/java/com/community/system/controller/QuizController.java`
   - 后端举报接口：`backend/src/main/java/com/community/system/controller/ReportController.java`
   - 后端绑定接口：`backend/src/main/java/com/community/system/controller/BindingController.java`
   - 目标：能从一个按钮追到对应接口

5. 看积分商城与订单流程（闭环业务）
   - 前端商城：`frontend/src/views/PointMall.vue`
   - 前端订单：`frontend/src/views/MyOrders.vue`、`frontend/src/views/OrderDetail.vue`
   - 后端积分/订单接口：`backend/src/main/java/com/community/system/controller/PointController.java`
   - 后端订单实现：`backend/src/main/java/com/community/system/service/impl/OrderServiceImpl.java`
   - 目标：看懂“积分扣减 -> 订单创建 -> 物流查询”

6. 看管理员后台流程（审核与看板）
   - 后台布局：`frontend/src/views/admin/Layout.vue`
   - 数据看板：`frontend/src/views/admin/Dashboard.vue`
   - 举报审核：`frontend/src/views/admin/ReportManage.vue`
   - 绑定审核：`frontend/src/views/admin/BindingAudit.vue`
   - 劝导工单：`frontend/src/views/admin/AdvisoryOrderManage.vue`
   - 对应后端：`AdminController.java`、`ReportController.java`、`BindingController.java`
   - 目标：掌握管理员端主要业务入口

7. 看核心业务实现（服务层）
   - 举报处理：`backend/src/main/java/com/community/system/service/impl/ReportServiceImpl.java`
   - 积分处理：`backend/src/main/java/com/community/system/service/impl/PointServiceImpl.java`
   - 答题处理：`backend/src/main/java/com/community/system/service/impl/QuizServiceImpl.java`
   - 目标：看清业务规则（限次、奖励、状态流转）

8. 看数据表结构与种子数据（理解“数据从哪来”）
   - `database/init.sql`
   - 目标：理解 user/report/exchange_order/community_binding_request 等表关系

9. 看系统配置（环境迁移必看）
   - `backend/src/main/resources/application.yml`
   - `frontend/vite.config.ts`
   - 目标：掌握数据库、Redis、代理、第三方 key 的配置入口

10. 最后看设计文档（补全业务背景）
   - `docs/01_需求规格说明书.md`
   - `docs/03_系统架构图.md`
   - `docs/04_系统设计与接口定义.md`
   - `docs/05_数据库ER图与建模.md`

阅读小技巧：
- 每看完一个页面，就用 `request.xxx('/api/...')` 反查对应后端 Controller。
- 每看完一个 Controller，再跳到对应 `service/impl` 看真实业务逻辑。
- 每看不懂一个字段，回到 `database/init.sql` 对照表结构和示例数据。

## ✅ 环境要求
- Docker Desktop（已启动）
- 建议可用内存 >= 4GB
- 可用端口：`3000`、`8080`、`3306`、`6379`

## 🚀 快速启动 (Docker)
1. 克隆并进入项目根目录：
   ```bash
   cd /你的项目路径/label-658
   ```
2. 一键构建并启动：
   ```bash
   docker compose up --build -d
   ```
3. 查看容器状态（都为 `Up` 即正常）：
   ```bash
   docker compose ps
   ```
4. 访问系统：
   - 前端首页：http://localhost:3000
   - 后端接口基地址：http://localhost:8080/api
5. 查看后端日志（可选）：
   ```bash
   docker compose logs -f backend
   ```

## 🧪 测试账号 (如有)
本项目为“手机号 + 短信验证码”登录，无固定密码。

- 管理员账号（已内置种子数据）
  - 手机号：`13800138001`
  - 验证码：`123456`
  - 登录后自动进入管理后台
- 居民账号（已内置种子数据）
  - 手机号：`13800138000`
  - 验证码：`123456`
  - 登录后进入居民首页

说明：
- 后端测试环境验证码固定为 `123456`（5 分钟有效逻辑已实现，同时也允许直接输入 `123456`）。
- 使用其他手机号登录会自动注册为居民角色（`USER`）。

## 📸 功能介绍
### 居民端
- 登录与鉴权：手机号验证码登录，JWT 鉴权
- 每日答题打卡：答题得积分，支持连续打卡展示
- 违规举报：可上传图片、填写描述、提交位置
- 社区绑定申请：居民提交认证，等待管理员审核
- 积分商城：积分兑换商品并生成订单
- 我的订单：查看订单详情与模拟物流轨迹
- AI 问答：接入第三方对话能力（可配置）

### 管理端
- 数据看板：居民总数、积分总量、待处理工单、答题统计图
- 居民管理：分页查看用户
- 举报审核：举报列表、通过/驳回、生成劝导工单
- 绑定审核：审核社区绑定申请
- 劝导工单管理：查看工单列表
- 虚拟设备大屏：设备分布可视化（含模拟数据）

## 🔐 默认服务与端口
- 前端（Nginx）：`3000 -> 80`
- 后端（Spring Boot）：`8080`
- MySQL：`3306`
- Redis：`6379`

## ⚙️ 本地开发启动（非 Docker，可选）
如果你想边改代码边调试，可按下面方式分别启动。

### 1) 启动 MySQL 与 Redis
可直接使用 Docker 启动这两个基础服务：
```bash
docker compose up -d db redis
```

### 2) 启动后端
```bash
cd backend
mvn spring-boot:run
```
默认读取：
- DB: `jdbc:mysql://localhost:3306/community_system`
- Redis: `localhost:6379`

可通过环境变量覆盖：
- `DB_HOST`、`DB_USER`、`DB_PASSWORD`
- `REDIS_HOST`、`REDIS_PASSWORD`

### 3) 启动前端
```bash
cd frontend
npm install
npm run dev
```
如果你是“本机直跑前后端”（不是前端容器），请先把 `frontend/vite.config.ts` 中代理目标改为：
```ts
target: 'http://localhost:8080'
```
否则会因 `backend` 域名无法解析导致接口请求失败。

访问：http://localhost:3000

## 🧠 第三方 API 配置（可选）
文件：`backend/src/main/resources/application.yml`

可配置：
- 高德逆地理编码：`thirdparty.amap.key`
- 百度 UNIT 对话：`thirdparty.baidu.client-id`、`thirdparty.baidu.client-secret`

未配置时系统仍可启动，但以下能力会受影响：
- 举报地址解析可能显示“未知定位地址”
- AI 问答可能返回降级提示

## 🗂 数据库连接信息（开发常用）
- Host: `localhost`
- Port: `3306`
- Database: `community_system`
- Username: `root`
- Password: `root`

## 🗄️ 数据初始化说明
- 首次启动 `db` 容器时会自动执行 `database/init.sql`
- 会自动创建：
  - 全部业务表（用户、题库、积分、举报、商城订单、绑定审核、劝导工单等）
  - 管理员与居民测试数据
  - 示例题库、商品、举报、打卡统计数据

## 🔍 启动后建议验证（5 分钟）
1. 打开 `http://localhost:3000`
2. 用管理员手机号 `13800138001` + 验证码 `123456` 登录
3. 进入“数据看板”，确认图表和统计加载成功
4. 退出后用居民账号 `13800138000` 登录
5. 在首页完成一次答题，进入“积分商城”尝试兑换

## 🩹 常见问题排查
1. 前端打不开（3000 端口占用）
   - 执行 `lsof -i :3000` 查占用进程，释放后重启
2. 后端报数据库连接失败
   - 确认 `db` 容器已 `Up`
   - 确认后端环境变量 `DB_HOST=db`（Docker 场景）
3. 修改了 `database/init.sql` 但未生效
   - 需要清理数据卷并重建：
     ```bash
     docker compose down -v
     docker compose up --build -d
     ```
4. AI 问答返回不可用
   - 检查 `application.yml` 的百度配置是否正确

## 📘 接口文档说明
- 本项目当前未集成 Swagger/OpenAPI 可视化页面（即没有 `/swagger-ui` 或 `/docs`）。
- 推荐使用前端页面联调，或使用 Postman / Apifox 访问 `http://localhost:8080/api/*`。

## 🧹 停止项目
```bash
docker compose down
```

彻底清理（含数据库数据）：
```bash
docker compose down -v
```

## 📚 参考文档
- 需求说明：`docs/01_需求规格说明书.md`
- 系统架构：`docs/03_系统架构图.md`
- 系统设计：`docs/04_系统设计与接口定义.md`
- 数据库建模：`docs/05_数据库ER图与建模.md`
