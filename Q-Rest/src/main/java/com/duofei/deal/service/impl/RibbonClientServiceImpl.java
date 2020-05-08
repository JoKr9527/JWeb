package com.duofei.deal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * ribbon 实现负载均衡远程调用
 * @author duofei
 * @date 2020/5/7
 */
@Service
public class RibbonClientServiceImpl extends AbstractProviderService {

    @Autowired
    public RibbonClientServiceImpl(DiscoveryClient discoveryClient,@Qualifier("loadBalancedRestTemplate") RestTemplate loadBalancedRestTemplate) {
        super.eurekaDiscoveryClient = discoveryClient;
        super.restTemplate = loadBalancedRestTemplate;
    }
}
