package com.servicecenter.service_center_management;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Health Check", description = "API health check endpoint")
class TestController {
    
    @GetMapping("/test")
    @Operation(
        summary = "Health check",
        description = "Simple endpoint to verify that the application is running"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Application is running"
        )
    })
    public String test() {
        return "Application is running on port 9090!";
    }
}