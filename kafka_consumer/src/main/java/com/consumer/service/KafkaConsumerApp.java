package com.consumer.service;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@Service
public class KafkaConsumerApp {
	
	@Autowired
	ServiceCaller serviceCaller;
	
	@Autowired
	private EurekaClient eurekaClient;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	KafkaConsumer<String, String> consumer;
	
	ConsumerRecords<String, String> records;
	
	private Properties setDefaultProperties() {
	     Properties props = new Properties();
	     props.put("bootstrap.servers", "localhost:9092,localhost:9093");
	     props.put("group.id", "test");
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     
	     return props;
	}

	public void receiveProcessSend(String topicName) {

	     consumer = new KafkaConsumer<>(setDefaultProperties());
	     
	     ArrayList<String> topics = new ArrayList<>();
	     topics.add(topicName);
	     //consumer receives data here
	     consumer.subscribe(topics);
	     
	     try {
	    
		     while (true) {
		         records = consumer.poll(100);
		         for (ConsumerRecord<String, String> record : records) {
		             System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
		             //topic name is hard coded here
		             sendProcessedData("processedData", record.value());
		             serviceCaller.getEmployeeService(record.value());
		         }
		     }
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     } finally {
	    	 consumer.close();
	     }

	}
	
	/*
	 * Sends processed data back to broker to new topic that holds processed data
	 */
	private void sendProcessedData(String processedDataTopicName, String recordValue) {
		
		Application producerService = eurekaClient.getApplication("producer-service");
		InstanceInfo instanceInfo = producerService.getInstances().get(0);
		String url = 
			"http://" + instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + "/producer/" + processedDataTopicName +"/"+ recordValue;
		String response = restTemplate.getForObject(url, String.class);

		System.out.println("=================RESPONSE: " + response + "==========");
	}
	
}
