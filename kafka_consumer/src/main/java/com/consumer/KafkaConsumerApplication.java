package com.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;

import com.consumer.service.KafkaConsumerApp;

@SpringBootApplication
@EnableEurekaClient
public class KafkaConsumerApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(KafkaConsumerApplication.class, args);
		
		KafkaConsumerApp consumerApp = (KafkaConsumerApp) context.getBean(KafkaConsumerApp.class);
		
		//receive data first + while look with sysout to process/check messages are present
		//this method also sends processed data to topic: processedData
		String inputTopicName = "inputData";
		consumerApp.receiveProcessSend(inputTopicName);

		
		
	}
}
