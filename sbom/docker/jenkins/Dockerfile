FROM jenkins/jenkins:2.452.3-lts

LABEL maintainer="dhinojosa@evolutionnext.com"
ENV JENKINS_USER=admin
ENV JENKINS_PASS=admin123

USER root
RUN apt-get update && apt-get install -y maven
# drop back to the regular jenkins user - good practice
USER jenkins
RUN jenkins-plugin-cli --plugins "dependency-track docker-plugin docker-workflow blueocean"
