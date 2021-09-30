#!/usr/bin/env sh

GRADLE_CLEAN=true
GRADLE_INSTALL=true

build_s2s_image() {
    git clone https://github.com/hmcts/s2s-test-tool.git
    cd s2s-test-tool
    git checkout allow-all-microservices
    ./gradlew build
    docker build -t hmcts/service-token-provider .
    cd .. && rm -rf s2s-test-tool
}

build_service_auth_app() {
    git clone https://github.com/hmcts/service-auth-provider-app.git
    cd service-auth-provider-app
    ./gradlew build
    docker build -t hmcts/service-token-provider .
    cd .. && rm -rf service-auth-provider-app
}

clean_old_docker_artifacts() {

    docker stop rd-judicial-api
    docker stop rd-judicial-db
    docker stop service-token-provider

    docker rm rd-judicial-api
    docker rm rd-judicial-db
    docker rm service-token-provider

    docker rmi hmcts/rd-judicial-api
    docker rmi hmcts/rd-judicial-db
    docker rmi hmcts/service-token-provider

    docker volume rm rd-judicial-api_rd-judicial-db-volume
}

execute_script() {

  clean_old_docker_artifacts

  build_s2s_image

  build_service_auth_app

  ./gradlew clean assemble

  export SERVER_PORT="${SERVER_PORT:-8090}"

  pwd

  chmod +x bin/*

  docker-compose up
}

execute_script
