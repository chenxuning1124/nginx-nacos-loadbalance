package org.reserved.loadbalance;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author chenxuning
 */
@Configuration
public class NamingServiceConfiguration {

    @Bean
    public NamingService initNamingService(PropertyUtil propertiesPojo) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", propertiesPojo.getNacosServerAddr());

        String namespace = propertiesPojo.getNacosNamespace();
        if (namespace != null) {
            properties.setProperty("namespace", namespace);
        }
        return NamingFactory.createNamingService(properties);
    }
}
