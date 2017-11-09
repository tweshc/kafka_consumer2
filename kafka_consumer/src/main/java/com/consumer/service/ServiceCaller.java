package com.consumer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@Component
public class ServiceCaller {

	@Autowired
	private EurekaClient eurekaClient;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	
	public void getEmployeeService(String data) {
		
		if(data.toLowerCase().contains("employee")) {
			
			Application employeeService = eurekaClient.getApplication("employeeservice");
			
			InstanceInfo instanceInfo = employeeService.getInstances().get(0);
			
			String url = "http://"+ instanceInfo.getIPAddr() + ":" + instanceInfo.getPort() + "/employeeService";
			
			String response = restTemplate.getForObject(url, String.class);
		}
	}
	
}

