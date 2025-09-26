package com.servicecenter.service_center_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ServiceCenterManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceCenterManagementApplication.class, args);
	}

}

@RestController
class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Application is running on port 9090!";
    }
}