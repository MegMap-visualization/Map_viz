SRC_DIR=.

# 说明
# IMAGE_URL 是镜像名称,已经定义 默认值是reg.zzcrowd.com/{REGISTRY_NAMESPACE}/{PROJECT_NAME}
# REGISTRY_NAMESPACE是项目所在仓库namespace,如本项目为platform
# DOCKER_TAG 是对应镜像tag，已经定义，格式有两种，如直接打tag Docker_tag为具体的版本号，如果无tag就是分支名—commit号

.PHONY: build

build: dockerfile-prepare
	docker build -t $(IMAGE_URL):$(DOCKER_TAG) --build-arg COMMIT_ID=$(COMMIT_ID) --cache-from $(IMAGE_URL):latest .

dockerfile-prepare:
	mvn clean && mvn install && mvn package

test:
	echo "Testing for project $(DOCKER_TAG)"
