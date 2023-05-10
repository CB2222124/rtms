FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /rtms

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/rtms/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /rtms/lib
COPY --from=build ${DEPENDENCY}/META-INF /rtms/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /rtms
ENTRYPOINT ["java","-cp","rtms:rtms/lib/*","com.github.cb2222124.rtms.RtmsApplication"]
