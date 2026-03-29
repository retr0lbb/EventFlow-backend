# ==========================================
# Estágio 1: Build da aplicação
# ==========================================
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copia arquivos de dependência primeiro (cache de camadas Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==========================================
# Estágio 2: Runtime
# ==========================================
FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app

# Cria um usuário não-root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia o JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Define usuário não-root
USER appuser

# Expõe a porta da aplicação
EXPOSE 8080

# Health check - verifica se a porta está respondendo
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080 || exit 1

# Inicia a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
