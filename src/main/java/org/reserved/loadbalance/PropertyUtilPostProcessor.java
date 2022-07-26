package org.reserved.loadbalance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.reserved.exception.InitConfigException;
import org.reserved.exception.ParameterException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chenxuning
 */
@Component
public class PropertyUtilPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (RegexConstant.PROPERTY_BEAN_NAME.equals(beanName)) {
            Environment environment = configurableApplicationContext.getEnvironment();
            String configLocation = environment.getProperty("configLocation");
            if (StringUtils.isBlank(configLocation)) {
                throw new ParameterException("Can not find the important parameter 'configLocation'");
            }
            FileInputStream inputStream = null;
            InputStream in = null;
            try {
                inputStream = new FileInputStream(configLocation);
                in = new BufferedInputStream(inputStream);
                StringBuilder out = new StringBuilder();
                byte[] b = new byte[1024 * 5];
                for (int n; (n = in.read(b)) != -1; ) {
                    out.append(new String(b, 0, n));
                }
                return PropertyUtil.verifyParameter(JSON.parseObject(out.toString(), PropertyUtil.class));
            } catch (JSONException e) {
                throw new InitConfigException("There is a syntax error with your config.properties", e);
            } catch (IOException e) {
                throw new InitConfigException("An exception occurs when loading the config.properties", e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    throw new InitConfigException("An exception occurs when loading the config.properties", e);
                }
            }
        }
        return null;
    }
}
