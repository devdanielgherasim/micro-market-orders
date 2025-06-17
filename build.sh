#!/bin/bash

CONTAINER_REGISTRY_NAME=${CONTAINER_REGISTRY_NAME:-""}
ARM_CLIENT_ID=${ARM_CLIENT_ID:-""}
ARM_CLIENT_SECRET=${ARM_CLIENT_SECRET:-""}
CI_COMMIT_SHA=${CI_COMMIT_SHA:-"test"}
CI_PROJECT_NAME=${CI_PROJECT_NAME:-""}
PROJECT_NAMESPACE=${PROJECT_NAMESPACE:-"microservices1691717"}

if [ -z "$CONTAINER_REGISTRY_NAME" ]; then
  echo "Error: CONTAINER_REGISTRY_NAME environment variable is not set"
  echo "Usage: CONTAINER_REGISTRY_NAME=myregistry.azurecr.io ./build.sh"
  exit 1
fi

if [ -z "$MAIN_SCRIPT_LOGIN" ]; then
  if [ ! -z "$ARM_CLIENT_ID" ] && [ ! -z "$ARM_CLIENT_SECRET" ]; then
    echo "===== Logging in to Container Registry ====="
    echo "$ARM_CLIENT_SECRET" | docker login $CONTAINER_REGISTRY_NAME -u "$ARM_CLIENT_ID" --password-stdin
    if [ $? -ne 0 ]; then
      echo "Error: Failed to log in to Container Registry"
      exit 1
    fi
  else
    echo "Warning: ARM_CLIENT_ID or ARM_CLIENT_SECRET not set. You may need to log in to ACR manually."
  fi
fi



MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end --show-version -DinstallAtEnd=true -DdeployAtEnd=true"

echo "===== Building Native Quarkus Application ====="
echo "Project: $PROJECT_NAMESPACE/$CI_PROJECT_NAME"
echo "Commit: $CI_COMMIT_SHA"
echo "Registry: $CONTAINER_REGISTRY_NAME"

mvn ${MAVEN_CLI_OPTS} clean package -Dnative \
  -T 1C \
  -Dquarkus.native.container-build=true \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=true \
  -Dquarkus.container-image.registry=${CONTAINER_REGISTRY_NAME} \
  -Dquarkus.container-image.tag=${CI_COMMIT_SHA} \
  -Dquarkus.container-image.group=${PROJECT_NAMESPACE} \
  -Dquarkus.container-image.name=${CI_PROJECT_NAME} \
  -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 \
  -DskipTests

echo "===== Build completed successfully ====="
echo "Docker image: ${CONTAINER_REGISTRY_NAME}/${PROJECT_NAMESPACE}/${CI_PROJECT_NAME}:${CI_COMMIT_SHA}"