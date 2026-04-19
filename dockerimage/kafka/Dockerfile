FROM eclipse-temurin:17-jdk-jammy

# Set Kafka version
ENV KAFKA_VERSION=4.2.0
ENV SCALA_VERSION=2.13

# Install tools
RUN apt-get update && apt-get install -y wget tar && rm -rf /var/lib/apt/lists/*

# Download Kafka
RUN wget https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz \
    && tar -xzf kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz \
    && mv kafka_${SCALA_VERSION}-${KAFKA_VERSION} /opt/kafka \
    && rm kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz

# Set working directory to bin (your requirement ✅)
WORKDIR /opt/kafka/bin

# Expose ports
EXPOSE 9092 9093

# Start Kafka (KRaft mode)
CMD bash -c "\
if [ ! -f /var/lib/kafka/data/meta.properties ]; then \
  /opt/kafka/bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c /opt/kafka/config/kraft/server.properties; \
fi && \
./kafka-server-start.sh /opt/kafka/config/kraft/server.properties"