package com.duofei.deal.service.impl;

import com.duofei.deal.bean.GoodsInfo;
import com.duofei.deal.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import java.net.URI;
import java.util.List;

/**
 * 基于 EurekaClient 实现远程服务调用
 * @author duofei
 * @date 2020/4/15
 */
@Service
public class EurekaClientServiceImpl extends AbstractProviderService {

    @Autowired
    public EurekaClientServiceImpl(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        super.eurekaDiscoveryClient = discoveryClient;
        super.restTemplate = restTemplate;
    }
}
