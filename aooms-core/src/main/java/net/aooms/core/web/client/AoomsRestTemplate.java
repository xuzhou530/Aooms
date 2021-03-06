package net.aooms.core.web.client;

import cn.hutool.core.bean.BeanUtil;
import net.aooms.core.AoomsVar;
import net.aooms.core.databoss.DataResult;
import net.aooms.core.databoss.DataResultStatus;
import net.aooms.core.property.PropertyAooms;
import net.aooms.core.property.PropertyServer;
import net.aooms.core.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

/**
 * 服务请求客户端
 * Created by 风象南(yuboon) on 2018-02-24
 */
public class AoomsRestTemplate {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate loadBalancedRestTemplate;

    @Autowired
    private PropertyAooms propertyAooms;

    @Autowired
    private PropertyServer serverProperties;

    private Boolean useRegistry;

    public DataResult get(String url) {
        return get(url, Collections.emptyMap());
    }

    public DataResult get(String url, Map<String, Object> params) {
        DataResult dataResult = new DataResult();
        Map map = null;
        if(useRegistry()){
            map = loadBalancedRestTemplate.getForObject(url,Map.class);
        }else{
            String serverUrl = getLocalServerUrl(url);
            if(logger.isInfoEnabled()){
                logger.info("Convert " + url + " -> " + serverUrl);
            }
            map = restTemplate.getForObject(serverUrl,Map.class,params);
        }

        Map mapStatus = (Map) map.get(AoomsVar.RS_META);
        DataResultStatus status = BeanUtil.mapToBean(mapStatus,DataResultStatus.class,true);
        map.put(AoomsVar.RS_META,status);
        dataResult.setData(map);
        return dataResult;
    }

    public DataResult post(String url) {
        return post(url,Collections.emptyMap());
    }

    public DataResult post(String url, Map<String, Object> params) {
        DataResult dataResult = new DataResult();
        Map map = null;
        if(useRegistry()){
            map = loadBalancedRestTemplate.getForObject(url,Map.class);
        }else{
            String serverUrl = getLocalServerUrl(url);
            if(logger.isInfoEnabled()){
                logger.info("onvert " + url + " -> " + serverUrl);
            }
            map = restTemplate.postForObject(serverUrl,params,Map.class);
        }

        Map mapStatus = (Map) map.get(AoomsVar.RS_META);
        DataResultStatus status = BeanUtil.mapToBean(mapStatus,DataResultStatus.class,true);
        map.put(AoomsVar.RS_META,status);
        dataResult.setData(map);
        return dataResult;
    }

    public DataResult upload(String url, Map<String, Object> params, Map<String, File> uploadFiles) {
        params.putAll(uploadFiles);
        return this.post(url,params);
    }

    // 是否使用注册中心
    public boolean useRegistry(){
        if(null == useRegistry)
            return useRegistry = (!propertyAooms.getRest().isLocal());
        return useRegistry;
    }

    // 获取本地服务真实地址,本地集成部署或调试时使用
    private String getLocalServerUrl(String url){
        try {
            URI uri = new URI(url);
            StringBuilder builder = new StringBuilder();
            builder
                .append(uri.getScheme())
                .append(":")
                .append("//")
                .append("localhost:")
                .append(propertyAooms.getRest().getLocalPort())
                .append(uri.getPath())
            ;

            return builder.toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("server url : "+ url +" is invalid !",e);
        }
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }


}