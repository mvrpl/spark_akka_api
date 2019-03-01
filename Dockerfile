FROM centos:latest

ARG USUARIO=mvrpl

RUN yum -y groupinstall 'Development Tools'

RUN yum -y install ruby sudo which vim

RUN useradd -m $USUARIO

USER $USUARIO
WORKDIR /home/$USUARIO

ENV PATH=/home/$USUARIO/.linuxbrew/bin:/home/$USUARIO/.linuxbrew/sbin:$PATH
ENV SHELL=/bin/bash
ENV USER=$USUARIO

RUN yes | ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Linuxbrew/install/master/install)"

ADD apache-spark.rb /home/$USUARIO/apache-spark.rb

RUN brew install --build-from-source apache-spark.rb

RUN brew install sbt

ADD app /home/$USUARIO/app

USER root

RUN chown -R $USUARIO:$USUARIO /home/$USUARIO