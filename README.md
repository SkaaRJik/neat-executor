# NEAT Prediction Service

## Project description

This project is designed to predict the data of the presented regression model. The neuroevolutionary algorithm ["Neuroevolution of augmenting topologies"(PDF)](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf) is used for forecasting.
The project is presented in a microservice architecture and includes the following components: 

1. [User's requests processing service](https://github.com/SkaaRJik/neatvue) (Should be run first)
2. [Data preprocessing service](https://github.com/SkaaRJik/neat-data-preprocessing)
3. [Prediction service](https://github.com/SkaaRJik/neat-executor)  <- You are here
4. [Graphical user interface](https://github.com/SkaaRJik/neatvue)
5. PostgresSQL 11
6. [SMB protocol (Linux: Samba)](#https://github.com/SkaaRJik/neat-user-requests/blob/separate-frontend/src/main/resources/sh-scripts/samba-instructions.md)
7. RabbitMQ 

## Service description

The service is responsible for predicting the preprocessed data. Uses the NEAT neuroevolution algorithm. 

## Minimal system requirements

<ol>
<li>CPU: 6 core 2.5 Ghz </li>
<li>RAM: 8Gb</li>
<li>Free disk space: 10GB</li>
</ol>

## Requirements

<ol>
<li>Java 11</li>
<li>Maven</li>
<li>RabbitMQ</li>
<li>Samba</li>
</ol>

## Running

<ol>
<li>Run RabbitMQ if it is not active</li>
<li>Run Samba if it is not active</li>
<li>Configure connections: src/main/resources/application.properties</li>
<li>Run build app</li>

`mvn spring-boot:build-image`

<li>After build - run server:</li>

`java -jar ./target/neatvue-0.0.1-SNAPSHOT.jar`

</ol>

## Samba configuration

See [samba instruction](https://github.com/SkaaRJik/neat-user-requests/blob/separate-frontend/src/main/resources/sh-scripts/samba-instructions.md)
