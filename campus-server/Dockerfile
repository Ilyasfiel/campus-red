FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/campus-server-1.0.0.jar app.jar

RUN mkdir -p /data/uploads

EXPOSE 8080

ENV DB_URL="jdbc:mysql://mysql:3306/campus_red?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai"
ENV DB_USERNAME="root"
ENV UPLOAD_PATH="/data/uploads"
ENV JWT_SECRET="change-me-in-production"

ENTRYPOINT ["java", "-jar", "app.jar"]
