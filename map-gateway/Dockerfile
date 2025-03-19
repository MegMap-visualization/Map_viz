FROM mcd.io/base/openjdk:11.0.6-jre-slim
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD ./target/map-gateway-1.0-SNAPSHOT.jar app.jar
ARG COMMIT_ID=123456
RUN echo "${COMMIT_ID}" >> /Version 

# 添加调试命令
RUN ls -l /
RUN ls -l /gateway.jar 
