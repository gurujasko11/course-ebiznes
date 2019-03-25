FROM ubuntu:18.04

MAINTAINER Jan Markiewicz <janusz.markiewicz@student.uj.edu.pl>

RUN useradd janusz --create-home

RUN apt-get update
RUN apt-get install -y vim unzip curl git

RUN apt-get install -y default-jdk scala gnupg2

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
RUN apt-get update
RUN apt-get -y install sbt

USER janusz

CMD echo "Hello World"
CMD java -version
CMD scala -version
