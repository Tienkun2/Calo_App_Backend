# Sử dụng JDK 21 từ Eclipse Temurin
FROM eclipse-temurin:21-jdk-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy toàn bộ source code vào container
COPY . .

# Biên dịch ứng dụng bằng Maven (bỏ qua test để build nhanh hơn)
RUN ./mvnw clean package -DskipTests

# Chạy ứng dụng
CMD ["java", "-jar", "target/CaloApp-0.0.1-SNAPSHOT.jar"]
