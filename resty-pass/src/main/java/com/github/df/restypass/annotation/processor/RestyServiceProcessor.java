package com.github.df.restypass.annotation.processor;

import com.github.df.restypass.annotation.RestyService;
import com.github.df.restypass.command.RestyCommandConfig;
import com.github.df.restypass.lb.rule.VersionRule;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * RestyService 注解处理器
 * 设置RestyCommandConfig
 * Created by darrenfu on 17-6-24.
 */
@SuppressWarnings("WeakerAccess")
public class RestyServiceProcessor implements RestyAnnotationProcessor {


    @Override
    public RestyCommandConfig processor(Annotation annotation, RestyCommandConfig properties) {

        if (annotation != null && annotation.annotationType().equals(RestyService.class)) {

            RestyService restyService = (RestyService) annotation;
            // 设置服务名称
            setServiceName(restyService, properties);
            // 设置重试次数
            setRetry(restyService, properties);
            // 设置降级服务
            setFallback(restyService, properties);
            //设置是否打开断路器
            setCircuitBreak(restyService, properties);
            //设置负载均衡器
            setLoadBalancer(restyService, properties);
            //设置工厂类
            setFactory(restyService, properties);
            //设置流量
            setLimit(restyService, properties);
            //设置路由版本
            setVersion(restyService, properties);
        }
        return properties;
    }

    /**
     * Sets service name.
     *
     * @param restyService the resty service
     * @param properties   the properties
     */
    protected void setServiceName(RestyService restyService, RestyCommandConfig properties) {
        String serviceName = restyService.serviceName();
        if (StringUtils.isEmpty(serviceName)) {
            throw new IllegalArgumentException("service name can not be null");
        }
        properties.setServiceName(serviceName);
    }

    /**
     * Sets retry.
     *
     * @param restyService the resty service
     * @param properties   the properties
     */
    protected void setRetry(RestyService restyService, RestyCommandConfig properties) {
        int retry = restyService.retry();
        if (retry < 0) {
            throw new IllegalArgumentException("retry must >0");
        }
        properties.setRetry(retry);


    }

    /**
     * Sets fallback.
     *
     * @param restyService the resty service
     * @param properties   the properties
     */
    protected void setFallback(RestyService restyService, RestyCommandConfig properties) {
        properties.setFallbackEnabled(restyService.fallbackEnabled());

        //fallback class
        properties.setFallbackClass(restyService.fallbackClass());
        // com.github.df.restypass.servertest.entity name
        if (StringUtils.isNotEmpty(restyService.fallbackBean())) {
            properties.setFallbackBean(restyService.fallbackBean());
        }
    }

    /**
     * Sets circuit break.
     *
     * @param restyService the resty service
     * @param properties   the properties
     */
    protected void setCircuitBreak(RestyService restyService, RestyCommandConfig properties) {
        // circuit break
        properties.setCircuitBreakEnabled(restyService.circuitBreakEnabled());

        if (restyService.forceBreakEnabled()) {
            properties.setCircuitBreakEnabled(true);
            properties.setForceBreakEnabled(true);
        } else {
            properties.setForceBreakEnabled(false);
        }
    }

    protected void setLoadBalancer(RestyService restyService, RestyCommandConfig properties) {
        properties.setLoadBalancer(restyService.loadBalancer());
    }

    protected void setFactory(RestyService restyService, RestyCommandConfig properties) {
//        properties.setFactory(restyService.factory());
    }

    protected void setLimit(RestyService restyService, RestyCommandConfig properties) {
        properties.setLimit(restyService.limit());
    }

    protected void setVersion(RestyService restyService, RestyCommandConfig properties) {
        List<VersionRule> versionRuleList = new ArrayList<>();

        for (String version : restyService.version()) {
            if (StringUtils.isNotEmpty(version)) {
                versionRuleList.add(VersionRule.create(restyService.serviceName(), version));
            }
        }
        if (versionRuleList.size() > 0) {
            properties.setVersion(versionRuleList);
        }
    }

}
