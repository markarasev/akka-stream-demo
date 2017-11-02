# Introduction to Akka-Streams

## Presentation slides

https://docs.google.com/presentation/d/e/2PACX-1vSbXRuXjTtfLCmNBm53qawMQNMchagzIOEQF8S_wvt0LzLRLv56XbiyLmaWiW6TI5sOizP42Rj0kWRn/pub?start=false&loop=false&delayms=3000

## Zookeeper / Kafka setup

 * Set `AFKA_ADVERTISED_HOST_NAME` to your docker host in `kafka-docker/docker-compose.yml`.
 * Run `docker-compose up` in the `kafka-docker` directory.
 
## Run
 * You can run whatever main class you want with: `sbt runMain <package.to.the.MainClass>`