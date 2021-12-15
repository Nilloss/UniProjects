FROM ubuntu:18.04

WORKDIR /usr/src/app

MAINTAINER Jack Nielsen

RUN apt-get update && apt-get install -y apt-utils && apt-get install -y curl

# Install Node.js
RUN apt-get install --yes curl
RUN curl -fsSL https://deb.nodesource.com/setup_14.x | bash -
RUN apt-get install --yes nodejs
RUN apt-get install --yes build-essential

COPY . ./

RUN cd client; npm install; npm run build;
RUN cd server; npm install; npm install express;

EXPOSE 80

WORKDIR /usr/src/app/server

CMD ["node", "index.js"]