package ch.hsr.dsl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public final static String TOPIC_NAME = "streams-plaintext-input2";
    public final static String GROUP_ID = "group_hsr";

    public static void main(String[] argv) throws Exception {
        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.LongDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
        configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //Figure out where to start processing messages from
        KafkaConsumer kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
        kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME));

        try {
            while (true) {
                ConsumerRecords<String, Long> records = kafkaConsumer.poll(100);
                for (ConsumerRecord<String, Long> record : records)
                    System.out.println(record.key() + "=" + record.value());
            }
        } catch (WakeupException ex) {
            System.out.println("Exception caught " + ex.getMessage());
        } finally {
            kafkaConsumer.close();
            System.out.println("After closing KafkaConsumer");
        }

    }
}
