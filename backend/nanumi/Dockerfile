# 사용할 베이스 이미지를 지정. Java 11 기반입니다.
FROM openjdk:11-jre-slim

# 스프링 부트 jar 파일을 Docker 이미지 안에 추가
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행시 사용할 포트를 지정 (기본적으로 8080을 사용하는 스프링 부트 애플리케이션을 위한 설정)
EXPOSE 8080

# 컨테이너가 시작되면 스프링 부트 애플리케이션을 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
