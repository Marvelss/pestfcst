package cn.edu.hdu.pestfcst.gatewayservice.controller;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final DiscoveryClient discoveryClient;

    public GatewayController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/services")
    public Map<String, List<String>> getServices() {
        Map<String, List<String>> services = new HashMap<>();
        discoveryClient.getServices().forEach(service -> {
            services.put(service, discoveryClient.getInstances(service)
                    .stream()
                    .map(instance -> instance.getUri().toString())
                    .toList());
        });
        return services;
    }

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "Gateway is running");
        return status;
    }
} 