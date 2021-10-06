# Cache gradle dependencies
FROM gradle:6.8-jdk15 as Cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY ./app/build.gradle /home/gradle/app-code/
WORKDIR /home/gradle/app-code/
RUN gradle clean build -i --stacktrace

# Build phase
FROM gradle:6.8-jdk15 as Builder
COPY --from=Cache /home/gradle/cache_home /home/gradle/.gradle
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY . $APP_HOME
RUN gradle build -i --stacktrace
