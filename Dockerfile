FROM mcd.io/base/openjdk:11.0.6-jre-slim
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD map-core/map-api/target/map-api-1.0.jar api.jar
ADD map-core/map-extract/target/map-extract-1.0.jar extract.jar
ADD map-gateway/target/map-gateway-1.0-SNAPSHOT.jar gateway.jar
ARG COMMIT_ID=123456
RUN echo "${COMMIT_ID}" >> /Version 
