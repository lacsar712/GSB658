# 后端构建阶段 (去除 alpine 以兼容 Apple M 芯片 ARM64)
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 为了加速国内下载构建，配置阿里云镜像
RUN echo '<settings><mirrors><mirror><id>aliyunmaven</id><mirrorOf>*</mirrorOf><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /usr/share/maven/ref/settings.xml

COPY pom.xml .
# 这一步下载所有依赖，利用Docker缓存机制加速后续构建
RUN mvn -s /usr/share/maven/ref/settings.xml dependency:go-offline || true

COPY src ./src
# 打包
RUN mvn -s /usr/share/maven/ref/settings.xml package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# 声明端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-Dfile.encoding=utf-8", "-jar", "app.jar"]
