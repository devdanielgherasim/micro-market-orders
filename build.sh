#!/usr/bin/env bash
set -euo pipefail

# build.sh for the orders service.
# Cloud-provider-aware: mirrors the registry-resolution/login logic in
# ../utilities/build.sh so this can run standalone or fanned out from there.

CLOUD_PROVIDER="${CLOUD_PROVIDER:-aws}"
ENVIRONMENT="${ENVIRONMENT:-dev}"
PROJECT_NAMESPACE="${PROJECT_NAMESPACE:-danielgherasim-microservices}"
CI_COMMIT_SHA="${CI_COMMIT_SHA:-$(git rev-parse --short HEAD 2>/dev/null || echo test)}"
CI_PROJECT_NAME="${CI_PROJECT_NAME:-orders}"

# Resolve the registry host, unless a calling script (e.g. ../utilities/build.sh)
# already resolved and exported CONTAINER_REGISTRY_NAME.
if [[ -z "${CONTAINER_REGISTRY_NAME:-}" ]]; then
  case "${CLOUD_PROVIDER}" in
    aws)
      : "${AWS_ACCOUNT_ID:?Set AWS_ACCOUNT_ID for AWS ECR image builds}"
      : "${AWS_REGION:?Set AWS_REGION for AWS ECR image builds}"
      CONTAINER_REGISTRY_NAME="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
      ;;
    azure)
      : "${CONTAINER_REGISTRY_NAME:?Set CONTAINER_REGISTRY_NAME environment variable, e.g. myregistry.azurecr.io}"
      ;;
    gcp)
      : "${GCP_REGION:?Set GCP_REGION for GCP Artifact Registry image builds}"
      : "${GCP_PROJECT_ID:?Set GCP_PROJECT_ID for GCP Artifact Registry image builds}"
      CONTAINER_REGISTRY_NAME="${GCP_REGION}-docker.pkg.dev/${GCP_PROJECT_ID}"
      ;;
    *)
      echo "Error: Unsupported CLOUD_PROVIDER '${CLOUD_PROVIDER}' (expected aws, azure, or gcp)" >&2
      exit 1
      ;;
  esac
fi

# Image path "group" segment, matching each cloud's registry layout exactly:
#   aws:   one ECR repo per service, named "<namespace>/<environment>/<service>"
#          (see infrastructure/terraform/aws/main.tf: aws_ecr_repository.application)
#   azure: single ACR, images pushed as "<namespace>/<service>"
#          (see infrastructure/terraform/azure/main.tf: azurerm_container_registry.this)
#   gcp:   single Artifact Registry repo "<namespace>-<environment>", image named "<service>"
#          (see infrastructure/terraform/gcp/main.tf: google_artifact_registry_repository.this)
case "${CLOUD_PROVIDER}" in
  aws)   IMAGE_GROUP="${PROJECT_NAMESPACE}/${ENVIRONMENT}" ;;
  azure) IMAGE_GROUP="${PROJECT_NAMESPACE}" ;;
  gcp)   IMAGE_GROUP="${PROJECT_NAMESPACE}-${ENVIRONMENT}" ;;
esac

# Skip login if already performed by a calling script (e.g. ../utilities/build.sh)
if [[ -z "${MAIN_SCRIPT_LOGIN:-}" ]]; then
  echo "===== Logging in to ${CLOUD_PROVIDER} container registry: ${CONTAINER_REGISTRY_NAME} ====="
  case "${CLOUD_PROVIDER}" in
    aws)
      : "${AWS_REGION:?Set AWS_REGION for AWS ECR login}"
      aws ecr get-login-password --region "${AWS_REGION}" |
        docker login --username AWS --password-stdin "${CONTAINER_REGISTRY_NAME}"
      ;;
    azure)
      : "${ARM_CLIENT_ID:?Set ARM_CLIENT_ID for Azure Container Registry login}"
      : "${ARM_CLIENT_SECRET:?Set ARM_CLIENT_SECRET for Azure Container Registry login}"
      docker login "${CONTAINER_REGISTRY_NAME}" -u "${ARM_CLIENT_ID}" -p "${ARM_CLIENT_SECRET}"
      ;;
    gcp)
      gcloud auth configure-docker "${GCP_REGION}-docker.pkg.dev" --quiet
      ;;
  esac
fi

MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end --show-version -DinstallAtEnd=true -DdeployAtEnd=true"

echo "===== Building Native Quarkus Application ====="
echo "Project: ${IMAGE_GROUP}/${CI_PROJECT_NAME}"
echo "Commit: ${CI_COMMIT_SHA}"
echo "Registry: ${CONTAINER_REGISTRY_NAME}"

mvn ${MAVEN_CLI_OPTS} clean package -Dnative \
  -T 1C \
  -Dquarkus.native.container-build=true \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=true \
  -Dquarkus.container-image.registry="${CONTAINER_REGISTRY_NAME}" \
  -Dquarkus.container-image.tag="${CI_COMMIT_SHA}" \
  -Dquarkus.container-image.group="${IMAGE_GROUP}" \
  -Dquarkus.container-image.name="${CI_PROJECT_NAME}" \
  -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-21 \
  -DskipTests

echo "===== Build completed successfully ====="
echo "Docker image: ${CONTAINER_REGISTRY_NAME}/${IMAGE_GROUP}/${CI_PROJECT_NAME}:${CI_COMMIT_SHA}"
