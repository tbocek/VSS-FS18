package ch.hsr.dsl;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Scanner;


/**
 * First start zookeeper:
 *
 * /opt/kafka_2.11-1.1.0$ bin/zookeeper-server-start.sh config/zookeeper.properties
 *
 * Next, start kafka:
 *
 * /opt/kafka_2.11-1.1.0$ bin/kafka-server-start.sh config/server.properties
 */

public class Producer {
    public final static String TOPIC_NAME = "streams-plaintext-input";

    public static void main(String[] argv) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter message(type exit to quit)");

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        org.apache.kafka.clients.producer.Producer producer = new KafkaProducer(configProperties);

        String line = in.nextLine();
        while (!line.equals("exit")) {
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>(TOPIC_NAME, line);
            producer.send(rec);
            line = in.nextLine();
        }

        in.close();
        producer.close();
    }
}
