FROM java:9-jre
ENV MAVEN_VERSION 3.3.9

RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn


WORKDIR /code

# # Prepare by downloading dependencies
ADD ./app/company-app/pom.xml /code/pom.xml
ADD ./app/base.db /db/base.db

RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

ADD ./app/company-app/src /code/src
RUN ["mvn", "package"]

EXPOSE 4567
CMD ["java", "-jar", "target/companyApp-jar-with-dependencies.jar"]