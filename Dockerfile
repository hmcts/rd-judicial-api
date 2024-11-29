ARG APP_INSIGHTS_AGENT_VERSION=3.4.9
ARG PLATFORM=""
FROM hmctspublic.azurecr.io/base/java${PLATFORM}:21-distroless

# Mandatory!
ENV APP rd-judicial-api.jar
ENV APPLICATION_TOTAL_MEMORY 512M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 48

# Optional
ENV JAVA_OPTS ""

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/$APP /opt/app/

WORKDIR /opt/app

EXPOSE 8090

CMD [ "rd-judicial-api.jar" ]

