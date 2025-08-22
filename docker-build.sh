#!/bin/bash

# Script para build y deploy de Randall IT con Docker
# Autor: Randall IT Team
# Versión: 1.0.0

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables
IMAGE_NAME="randall-it"
TAG="latest"
CONTAINER_NAME="randall-it-api"

echo -e "${BLUE}🚀 Randall IT - Docker Build & Deploy${NC}"
echo "=================================="

# Función para mostrar ayuda
show_help() {
    echo "Uso: $0 [OPCIÓN]"
    echo ""
    echo "Opciones:"
    echo "  build     - Construir imagen Docker"
    echo "  run       - Ejecutar contenedor"
    echo "  stop      - Detener contenedor"
    echo "  restart   - Reiniciar contenedor"
    echo "  logs      - Ver logs del contenedor"
    echo "  clean     - Limpiar contenedores e imágenes"
    echo "  compose   - Usar docker-compose"
    echo "  help      - Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 build"
    echo "  $0 run"
    echo "  $0 compose"
}

# Función para construir imagen
build_image() {
    echo -e "${YELLOW}🔨 Construyendo imagen Docker...${NC}"
    docker build -t ${IMAGE_NAME}:${TAG} .
    echo -e "${GREEN}✅ Imagen construida exitosamente: ${IMAGE_NAME}:${TAG}${NC}"
}

# Función para ejecutar contenedor
run_container() {
    echo -e "${YELLOW}🚀 Ejecutando contenedor...${NC}"
    docker run -d \
        --name ${CONTAINER_NAME} \
        -p 8080:8080 \
        -e JAVA_OPTS="-Xmx512m -Xms256m" \
        -e SPRING_PROFILES_ACTIVE="docker" \
        --restart unless-stopped \
        ${IMAGE_NAME}:${TAG}
    
    echo -e "${GREEN}✅ Contenedor ejecutándose: ${CONTAINER_NAME}${NC}"
    echo -e "${BLUE}📊 API disponible en: http://localhost:8080${NC}"
    echo -e "${BLUE}📚 Swagger UI: http://localhost:8080/swagger-ui/index.html${NC}"
}

# Función para detener contenedor
stop_container() {
    echo -e "${YELLOW}⏹️ Deteniendo contenedor...${NC}"
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    docker rm ${CONTAINER_NAME} 2>/dev/null || true
    echo -e "${GREEN}✅ Contenedor detenido y eliminado${NC}"
}

# Función para reiniciar contenedor
restart_container() {
    echo -e "${YELLOW}🔄 Reiniciando contenedor...${NC}"
    stop_container
    run_container
}

# Función para ver logs
show_logs() {
    echo -e "${YELLOW}📋 Mostrando logs del contenedor...${NC}"
    docker logs -f ${CONTAINER_NAME}
}

# Función para limpiar
clean_docker() {
    echo -e "${YELLOW}🧹 Limpiando contenedores e imágenes...${NC}"
    docker stop ${CONTAINER_NAME} 2>/dev/null || true
    docker rm ${CONTAINER_NAME} 2>/dev/null || true
    docker rmi ${IMAGE_NAME}:${TAG} 2>/dev/null || true
    echo -e "${GREEN}✅ Limpieza completada${NC}"
}

# Función para docker-compose
use_compose() {
    echo -e "${YELLOW}🐳 Usando docker-compose...${NC}"
    if [ "$1" = "up" ]; then
        docker-compose up -d
        echo -e "${GREEN}✅ Servicios iniciados con docker-compose${NC}"
    elif [ "$1" = "down" ]; then
        docker-compose down
        echo -e "${GREEN}✅ Servicios detenidos${NC}"
    elif [ "$1" = "logs" ]; then
        docker-compose logs -f
    else
        echo -e "${BLUE}📊 Comandos docker-compose disponibles:${NC}"
        echo "  $0 compose up    - Iniciar servicios"
        echo "  $0 compose down  - Detener servicios"
        echo "  $0 compose logs  - Ver logs"
    fi
}

# Función para verificar estado
check_status() {
    echo -e "${BLUE}📊 Estado del sistema:${NC}"
    echo "----------------------------------------"
    
    # Verificar imagen
    if docker images | grep -q ${IMAGE_NAME}; then
        echo -e "${GREEN}✅ Imagen Docker: ${IMAGE_NAME}:${TAG}${NC}"
    else
        echo -e "${RED}❌ Imagen Docker: No encontrada${NC}"
    fi
    
    # Verificar contenedor
    if docker ps | grep -q ${CONTAINER_NAME}; then
        echo -e "${GREEN}✅ Contenedor: ${CONTAINER_NAME} (Ejecutándose)${NC}"
    elif docker ps -a | grep -q ${CONTAINER_NAME}; then
        echo -e "${YELLOW}⚠️ Contenedor: ${CONTAINER_NAME} (Detenido)${NC}"
    else
        echo -e "${RED}❌ Contenedor: No encontrado${NC}"
    fi
    
    # Verificar puerto
    if netstat -an 2>/dev/null | grep -q ":8080 "; then
        echo -e "${GREEN}✅ Puerto 8080: En uso${NC}"
    else
        echo -e "${YELLOW}⚠️ Puerto 8080: No en uso${NC}"
    fi
}

# Procesar argumentos
case "$1" in
    "build")
        build_image
        ;;
    "run")
        run_container
        ;;
    "stop")
        stop_container
        ;;
    "restart")
        restart_container
        ;;
    "logs")
        show_logs
        ;;
    "clean")
        clean_docker
        ;;
    "compose")
        use_compose $2
        ;;
    "status")
        check_status
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        echo -e "${RED}❌ Opción no válida: $1${NC}"
        echo ""
        show_help
        exit 1
        ;;
esac
