# MapViz

Website code repository for meg-hdmap visualize and management.

#### 顺序：

## 服务启动顺序

1. Nacos: D:\Old_Map_backend\middleware\nacos\bin\startup.cmd -m standalone
2. Redis: net start redis
3. Elasticsearch: D:\Old_Map_backend\middleware\elasticsearch\bin\elasticsearch.bat
4. Gateway: mvn spring-boot:run
5. Map-api: mvn spring-boot:run

## 端口列表

- Nacos: 8848
- Redis: 6379
- MySQL: 3306
- Elasticsearch: 9200
- Gateway: 10000
- Map-api: 8000

### 编译项目

```bash
# 进入项目根目录
cd D:\Old_Map_backend\mapviz

mvn clean install -DskipTests
```

### 启动nacos服务

```bash
cd bin

.\startup.cmd -m standalone
```

### 启动 Elasticsearch

```bash
#进入 elasticsearch 的 bin 目录

cd elasticsearch\bin

.\elasticsearch.bat
```

### 启动 redis

```bash
#进入 redis 的 bin 目录

sc query redis
net start redis
redis-cli ping

.\redis-server.exe
```

### 启动gateway服务

```bash
cd mapviz\mapviz-gateway

mvn spring-boot:run
```

### 启动 mysql 服务

```bash
#连接mysql
mysql -u root -p
# 进入 mysql 的 bin 目录
cd mysql\bin

.\mysql.exe
```

### 启动 map-api 服务

```bash
# 进入 map-api 目录
cd mapviz\map-core\map-api

mvn spring-boot:run
```
