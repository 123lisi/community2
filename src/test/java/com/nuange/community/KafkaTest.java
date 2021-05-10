package com.nuange.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {
    @Autowired
    private KafkaProdeucer kafkaProdeucer;
    @Test
    public void testKafka(){
        kafkaProdeucer.sendMessage("test1","你好");
        kafkaProdeucer.sendMessage("test1","在吗？");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
@Component
class KafkaProdeucer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);

    }
}
@Component
class KafkaConsumer{
    @KafkaListener(topics = {"test1"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    };
}