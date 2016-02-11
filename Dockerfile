FROM heroku/jvm

ENV MAVEN_VERSION 3.3.9

RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn


WORKDIR /app/user/

# # Prepare by downloading dependencies
ADD ./app/company-app/pom.xml /app/user/pom.xml
ADD ./app/base.db /app/user/base.db

RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

ADD ./app/company-app/src /app/user/src
RUN ["mvn", "package"]