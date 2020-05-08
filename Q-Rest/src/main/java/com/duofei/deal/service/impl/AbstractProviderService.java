package com.duofei.deal.service.impl;

import com.duofei.deal.bean.GoodsInfo;
import com.duofei.deal.service.ProviderService;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * 基础的服务调用过程
 * @author duofei
 * @date 2020/5/7
 */
public abstract class AbstractProviderService implements ProviderService {

    protected RestTemplate restTemplate;

    protected DiscoveryClient eurekaDiscoveryClient;

    @Override
    public GoodsInfo queryGoodsInfo(String id) {
        ServiceInstance goodsService = getServiceInstance("Q-GOODS");
        return restTemplate.getForEntity(getURL(goodsService) + "/goods/query/" + id, GoodsInfo.class).getBody();
    }

    @Override
    public void createOrder(String userName, String goodsId, Integer num, Float total) {
        ServiceInstance orderService = getServiceInstance("Q-ORDER");
        URI uri = getURIBuilder(orderService).pathSegment("order", "createOrder", userName, goodsId).queryParam("num", num)
                .queryParam("total", total).build().toUri();
        restTemplate.getForEntity(uri, Void.class);
    }

    @Override
    public void userPay(String userName, Float total) {
        ServiceInstance userService = getServiceInstance("Q-USER");
        URI uri = getURIBuilder(userService).pathSegment("user", "pay", userName).queryParam("total", total).build().toUri();
        restTemplate.getForEntity(uri, Void.class);
    }

    @Override
    public void goodsReduce(String goodsId, Integer num) {
        ServiceInstance goodsService = getServiceInstance("Q-GOODS");
        URI uri = getURIBuilder(goodsService).pathSegment("goods", "reduce", goodsId).queryParam("num", num).build().toUri();
        restTemplate.getForEntity(uri, Void.class);
    }

    /**
     * 获取指定服务的 ServiceInstance
     * @author duofei
     * @date 2020/4/15
     * @param serviceId 服务名
     * @return 服务对应的 ServiceInstance
     */
    private ServiceInstance getServiceInstance(String serviceId){
        List<ServiceInstance> instances = eurekaDiscoveryClient.getInstances(serviceId);
        //此处没有做负载均衡，取返回的第一个实例即可
        return instances.get(0);
    }

    /**
     * 通过 ServiceInstance 获取请求地址
     * @author duofei
     * @date 2020/4/15
     * @param serviceInstance 服务信息实例
     * @return String 服务地址
     */
    protected String getURL(ServiceInstance serviceInstance){
        UriComponentsBuilder uriBuilder = getURIBuilder(serviceInstance);
        if(uriBuilder != null){
            return uriBuilder.build().toUriString();
        }
        return null;
    }

    protected UriComponentsBuilder getURIBuilder(ServiceInstance serviceInstance){
        if(serviceInstance instanceof EurekaServiceInstance){
            EurekaServiceInstance instance = (EurekaServiceInstance) serviceInstance;
            return UriComponentsBuilder.newInstance().scheme(instance.getScheme()).host(instance.getInstanceInfo().getIPAddr())
                    .port(instance.getPort());
        }
        return null;
    }
}
