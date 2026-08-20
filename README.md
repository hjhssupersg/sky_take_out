# 苍穹外卖（sky_take_out）

一个面向餐饮外卖业务的全栈单仓库项目，包含商家管理端、微信小程序用户端、Spring Boot 后端服务以及 Nginx 部署配置。

项目支持菜单浏览、购物车、地址簿、下单与订单查询等用户侧流程，以及员工、分类、菜品、套餐、订单处理、营业状态、工作台和经营报表等管理侧能力。后端同时集成 Redis 缓存、阿里云 OSS 文件存储、微信小程序登录、WebSocket 订单通知及 Excel 运营报表导出。

## 项目结构

```text
sky_take_out/
|-- apps/
|   |-- admin-web/                 # 商家管理端 Web 构建产物
|   |   `-- dist/                  # 可由 Nginx 直接托管的静态文件
|   `-- miniapp/                   # 微信小程序用户端源码
|       |-- pages/                 # 小程序页面
|       |-- components/            # 通用组件
|       |-- uni_modules/           # uni-app/uni-ui 相关模块
|       |-- common/                # 编译后的公共运行时代码
|       `-- static/                # 图片等静态资源
|-- services/
|   `-- backend/                   # Java 后端 Maven 聚合工程
|       |-- sky-common/            # 公共基础能力
|       |-- sky-pojo/              # 领域模型与接口数据对象
|       `-- sky-server/            # 可执行的 Spring Boot 服务
|-- deploy/
|   `-- nginx/
|       `-- nginx.conf             # 静态托管、反向代理和 WebSocket 配置
|-- .env.example                   # 运行环境变量清单，不包含真实密钥
|-- .gitignore
`-- README.md
```

`.local-backup` 和 `.local-runtime` 为本机迁移备份或运行环境目录，已被 Git 忽略，不属于项目源代码和正式交付内容。

## 技术栈

| 范畴 | 技术与版本 | 用途 |
| --- | --- | --- |
| 后端语言与框架 | Java 8、Spring Boot 2.7.3 | HTTP 服务、依赖注入、配置管理、任务调度 |
| Web 层 | Spring MVC、Spring WebSocket | REST API、拦截器、异常处理、订单消息推送 |
| 持久层 | MyBatis 2.2.0、MySQL、Druid | SQL 映射、关系型数据存储、连接池 |
| 缓存 | Spring Data Redis | 缓存及 Redis 数据访问 |
| 鉴权 | JWT（JJWT 0.9.1） | 管理员和用户的无状态身份认证 |
| 分页与工具 | PageHelper、Lombok、Fastjson、Apache Commons Lang | 分页查询、样板代码简化、JSON 与通用工具 |
| 文件服务 | 阿里云 OSS SDK | 菜品等资源的对象存储上传 |
| 微信能力 | 微信小程序登录、微信支付 HTTP Client | 小程序用户身份获取与支付能力集成基础 |
| 报表 | Apache POI | 运营数据 Excel 报表生成 |
| API 文档 | Knife4j | 接口文档与调试支持 |
| 管理端 | Vue 构建产物、Element UI 相关资源 | 商家后台页面的部署版本 |
| 用户端 | 微信小程序原生页面结构、uni-ui 组件 | 用户点餐和订单流程 |
| 网关与部署 | Nginx | 前端静态资源托管、接口反向代理、WebSocket 转发 |

## 总体架构

```text
                         +------------------------+
                         | 商家管理端 Web          |
                         | apps/admin-web/dist     |
                         +-----------+------------+
                                     |
                         +-----------v------------+
                         | 微信小程序用户端         |
                         | apps/miniapp            |
                         +-----------+------------+
                                     |
                       HTTP / HTTPS 与 WebSocket
                                     |
                         +-----------v------------+
                         | Nginx                    |
                         | 静态托管 / 路由转发       |
                         +-----------+------------+
                                     |
                     +---------------v----------------+
                     | Spring Boot: sky-server          |
                     | admin API / user API / WebSocket |
                     +---------------+----------------+
                                     |
             +-----------------------+-----------------------+
             |                       |                       |
   +---------v---------+   +---------v---------+   +---------v---------+
   | MySQL             |   | Redis             |   | OSS / 微信服务     |
   | 业务持久化数据     |   | 缓存与临时数据     |   | 外部平台能力       |
   +-------------------+   +-------------------+   +-------------------+
```

系统采用前后端分离架构。前端只负责展示、交互和调用接口；Nginx 统一接收请求，并将静态资源和动态接口分别处理；后端负责鉴权、业务规则、数据读写和外部服务调用。

### 请求与路由关系

| 外部路径 | Nginx 转发目标 | 用途 |
| --- | --- | --- |
| `/` | 管理端静态文件及 `index.html` 回退 | 支持管理端单页应用路由 |
| `/api/` | `http://127.0.0.1:8080/admin/` | 商家管理端 API |
| `/user/` | `http://127.0.0.1:8080/user/` | 微信小程序用户 API |
| `/ws/` | `http://127.0.0.1:8080/ws/` | 管理端订单 WebSocket 通知 |

后端默认监听 `8080` 端口。实际 Nginx 行为以 [`deploy/nginx/nginx.conf`](deploy/nginx/nginx.conf) 为准。

## 后端架构

后端位于 [`services/backend`](services/backend)，是一个 Maven 多模块工程：

```text
services/backend/
|-- pom.xml                 # 父 POM：统一版本、依赖管理和模块聚合
|-- sky-common/             # 公共模块
|-- sky-pojo/               # 数据模型模块
`-- sky-server/             # 服务启动和业务实现模块
```

### Maven 模块职责

| 模块 | 职责 | 典型内容 |
| --- | --- | --- |
| `sky-common` | 复用的基础设施与横切能力 | `Result`、`PageResult`、JWT 工具、OSS 工具、微信支付工具、统一异常、配置属性、常量、Jackson 配置 |
| `sky-pojo` | 跨层传输和持久化的数据对象 | `entity`、`DTO`、`VO` |
| `sky-server` | 应用启动、接口暴露和业务实现 | Controller、Service、Mapper、配置、拦截器、定时任务、WebSocket |

其中 [`SkyApplication.java`](services/backend/sky-server/src/main/java/com/sky/SkyApplication.java) 是 Spring Boot 启动入口。`sky-server` 依赖 `sky-common` 和 `sky-pojo`，从而将业务实现与通用能力、数据对象分离。

### 服务内部分层

`sky-server` 的主要包位于 `src/main/java/com/sky`：

| 层级/目录 | 作用 |
| --- | --- |
| `controller/admin` | 面向管理端的接口：员工、分类、菜品、套餐、订单、店铺状态、工作台、报表和文件上传。 |
| `controller/user` | 面向小程序的接口：用户登录、菜单、购物车、地址簿、下单、支付、订单查询和店铺状态。 |
| `service` / `service/impl` | 定义并实现核心业务规则，负责事务边界与跨表、跨服务协调。 |
| `mapper` | MyBatis Mapper 接口，封装数据库访问契约。 |
| `resources/mapper` | Mapper 对应的 XML SQL 文件，负责复杂 SQL 的具体映射。 |
| `config` | Web MVC、Redis 序列化、OSS 和 WebSocket 等框架配置。 |
| `interceptor` | 从请求头读取 JWT，分别完成管理员和用户身份校验。 |
| `aspect` | AOP 横切逻辑，例如基于自定义注解的公共字段自动填充。 |
| `annotion` | 项目内部自定义注解。目录名保留了原项目拼写。 |
| `handler` | 全局异常处理，将业务异常转换成统一接口响应。 |
| `task` | 定时业务处理，例如订单状态检查与关闭。 |
| `websocket` | WebSocket 服务端，向管理端推送订单相关事件。 |

一次典型 API 调用的处理顺序如下：

```text
客户端请求
  -> Nginx 路由
  -> Spring MVC 拦截器（JWT 身份验证）
  -> Controller（参数接收与接口编排）
  -> Service（业务规则、事务、缓存协同）
  -> Mapper + MyBatis XML（SQL 执行）
  -> MySQL / Redis / OSS / 微信等外部依赖
  -> Result 或 PageResult 统一响应
```

### 数据对象约定

数据模型位于 [`services/backend/sky-pojo/src/main/java/com/sky`](services/backend/sky-pojo/src/main/java/com/sky)：

| 类型 | 目录 | 含义 |
| --- | --- | --- |
| Entity | `entity` | 与数据库表对应的持久化实体，如员工、菜品、订单、购物车、地址簿。 |
| DTO | `dto` | 接收客户端请求参数的对象，如登录、分页查询、菜品编辑、订单提交。 |
| VO | `vo` | 返回给客户端的展示对象，如订单详情、菜品列表、经营统计、销售排行。 |

这种区分避免将数据库字段直接暴露给客户端，也使请求模型与响应模型能够独立演进。

### 业务能力划分

| 业务域 | 主要功能 |
| --- | --- |
| 员工与认证 | 管理员登录、员工账号维护、密码修改、JWT 鉴权。 |
| 商品中心 | 分类、菜品、口味、套餐及套餐菜品关系维护，上下架状态控制。 |
| 用户中心 | 微信小程序登录、收货地址簿管理。 |
| 交易链路 | 购物车、订单提交、支付信息、接单、拒单、取消、派送、完成和历史订单。 |
| 商家运营 | 店铺营业状态、工作台汇总、营业额/用户/订单/销量等数据报表。 |
| 通知与资源 | WebSocket 新订单提醒、OSS 文件上传、Excel 运营报表导出。 |

## 前端架构

### 管理端 Web

[`apps/admin-web`](apps/admin-web) 只保留了已经编译完成的管理端资源：

```text
apps/admin-web/dist/
|-- index.html
|-- js/                   # 已压缩的页面和第三方脚本
|-- css/                  # 已压缩的样式
|-- img/                  # 页面图片、图标
|-- media/                # 音频提示资源
`-- service-worker.js     # PWA/缓存相关脚本
```

该目录可作为 Nginx 的静态站点根目录直接部署。仓库中没有管理端的 `src`、`package.json` 和构建配置，因此当前版本**可以运行和部署，但不能直接重新构建或进行源码级开发**。如需继续开发管理端，应先补齐其原始前端工程。

### 微信小程序用户端

[`apps/miniapp`](apps/miniapp) 是可使用微信开发者工具打开的小程序工程：

| 目录/文件 | 作用 |
| --- | --- |
| `app.js`、`app.json`、`app.wxss` | 小程序全局脚本、页面注册和全局样式。 |
| `pages/` | 业务页面：首页点餐、订单、订单详情、支付、支付成功、地址管理、备注、个人中心、历史订单和网络异常页。 |
| `components/` | 可复用页面组件，例如状态栏、图标、弹窗与选择器。 |
| `uni_modules/` | 已引入的 uni-ui 模块，如过渡动画组件。 |
| `common/` | 小程序公共脚本与运行时代码。 |
| `static/` | Logo、订单状态、地址、支付等页面静态素材。 |
| `project.config.json` | 微信开发者工具项目配置。 |

页面由微信小程序的 `.js`、`.wxml`、`.wxss` 和 `.json` 文件组成，分别承载页面逻辑、结构、样式和局部配置。

## 配置与依赖

后端使用 Spring Profile 管理环境配置，默认启用 `dev`：

- [`application.yml`](services/backend/sky-server/src/main/resources/application.yml)：端口、MyBatis、日志、JWT 等通用配置。
- [`application-dev.yml`](services/backend/sky-server/src/main/resources/application-dev.yml)：开发环境的数据源、Redis、OSS、微信配置映射。
- [`.env.example`](.env.example)：环境变量名称和示例值。

需要在操作系统环境变量、IDE Run Configuration 或部署平台配置中提供以下变量：

| 类别 | 变量 |
| --- | --- |
| Spring | `SPRING_PROFILES_ACTIVE` |
| MySQL | `SKY_DB_HOST`、`SKY_DB_PORT`、`SKY_DB_NAME`、`SKY_DB_USERNAME`、`SKY_DB_PASSWORD` |
| Redis | `SKY_REDIS_HOST`、`SKY_REDIS_PORT`、`SKY_REDIS_DATABASE` |
| JWT | `SKY_JWT_ADMIN_SECRET`、`SKY_JWT_USER_SECRET` |
| 员工初始密码 | `SKY_EMPLOYEE_DEFAULT_PASSWORD` |
| 阿里云 OSS | `SKY_OSS_ENDPOINT`、`SKY_OSS_ACCESS_KEY_ID`、`SKY_OSS_ACCESS_KEY_SECRET`、`SKY_OSS_BUCKET_NAME` |
| 微信小程序 | `SKY_WECHAT_APPID`、`SKY_WECHAT_SECRET` |

请勿将真实数据库密码、JWT 密钥、OSS 凭据和微信密钥提交到 Git。Spring Boot 默认不会自动读取 `.env` 文件；若使用 `.env` 管理本地变量，需要由终端、IDE 或额外的环境加载工具将其导出到进程环境。

## 本地运行

### 前置条件

- JDK 8 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.x（或与 JDBC 驱动兼容的 MySQL 版本）
- Redis
- 微信开发者工具（运行小程序时需要）
- Nginx（部署管理端或模拟生产路由时需要）

启动前请创建数据库 `sky_take_out`、准备相应表结构和基础数据，并按 [`.env.example`](.env.example) 配置实际运行变量。

### 启动后端

```powershell
cd services/backend
mvn -pl sky-server -am spring-boot:run
```

`-pl sky-server -am` 表示运行 `sky-server` 模块，并自动构建其依赖的 `sky-common` 与 `sky-pojo` 模块。启动成功后，服务默认地址为 `http://localhost:8080`。

### 运行微信小程序

1. 使用微信开发者工具导入 [`apps/miniapp`](apps/miniapp)。
2. 根据本机后端地址配置小程序请求域名或开发环境地址。
3. 在微信开发者工具中编译、预览或上传。

`project.private.config.json` 属于本机私有配置，已被 Git 忽略，不应纳入版本控制。

### 部署管理端与反向代理

将 [`apps/admin-web/dist`](apps/admin-web/dist) 部署到 Nginx 的静态站点目录，并采用 [`deploy/nginx/nginx.conf`](deploy/nginx/nginx.conf) 作为配置基础。生产环境至少需要根据实际部署情况调整：

- `root`：管理端构建产物的实际绝对路径；
- `upstream backend`：后端服务的主机与端口；
- `server_name` 和 `listen`：域名、端口与 HTTPS 配置；
- 小程序合法请求域名、WebSocket 合法域名及相关证书配置。

## 开发注意事项

- 管理端的源码不在当前仓库，修改 `dist` 内的压缩文件只适合作为临时修补，无法替代正常的前端源码构建流程。
- 后端接口分为 `/admin/**` 和 `/user/**` 两个边界；新增接口时应按客户端身份放入对应 Controller 包，并配置匹配的鉴权策略。
- SQL Mapper 接口与 XML 映射文件需要保持方法名、参数名和返回类型一致。
- 新的跨接口公共对象优先放入 `sky-common`；实体、DTO、VO 放入 `sky-pojo`；实际业务实现留在 `sky-server`。
- 订单状态、缓存失效、WebSocket 推送属于跨层行为，改动订单相关逻辑时应一并评估。

## 许可证与说明

本仓库未包含许可证文件。使用、分发或二次开发前，请确认课程资料、原始项目资源和第三方依赖各自适用的授权条款。
