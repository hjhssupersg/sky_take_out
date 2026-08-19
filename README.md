# 苍穹外卖项目

这是一个单仓库项目，包含 Java 后端、微信小程序、现有的管理端构建产物和部署配置。

## 项目目录

```text
sky_take_out/
|-- apps/
|   |-- admin-web/       # 可部署的管理端构建产物，原始源码暂不可用
|   `-- miniapp/         # 微信小程序用户端
|-- services/
|   `-- backend/         # Spring Boot Maven 多模块后端
|-- deploy/
|   `-- nginx/           # Nginx 反向代理配置
|-- docs/                # 项目和变更文档
`-- .env.example         # 所需环境变量名称，不包含真实密钥
```

迁移备份和 Windows Nginx 运行环境存放在被 Git 忽略的目录
（`.local-backup` 和 `.local-runtime`）中，不属于仓库内容。

## 后端

运行要求：JDK 8 及以上、Maven、MySQL 和 Redis。

应用从环境变量读取凭据。请使用 `.env.example` 作为配置清单，但不要提交
填写了真实值的 `.env` 文件。Spring Boot 不会自动加载 `.env`，因此启动服务前，
请在终端或 IDE 的运行配置中设置这些变量。

```powershell
cd services/backend
mvn -pl sky-server -am spring-boot:run
```

## 管理端

`apps/admin-web/dist` 中的管理端应用是已经编译完成的 Vue 构建产物，可以通过
Nginx 部署，但由于原始 `package.json` 和 `src` 目录不可用，目前无法重新构建。

## 微信小程序

请使用微信开发者工具打开 `apps/miniapp`。本机的
`project.private.config.json` 文件会被 Git 忽略。

