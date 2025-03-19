FROM mcd.io/base/openjdk:11.0.6-jre-slim
ENV TZ=Asia/Shanghai
RUN mkdir -p /app/nacos
COPY ./* /app/nacos/
RUN ls
RUN chmod 777 /app/nacos/bin/shutdown.sh && chmod 777 /app/nacos/bin/startup.sh
