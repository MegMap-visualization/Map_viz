FROM mcd.io/base/openjdk:11.0.6-jre-slim
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD target/map-api-1.0.jar app.jar
RUN ls
ARG COMMIT_ID=123456
RUN echo "${COMMIT_ID}" >> /Version