package org.reserved.loadbalance;

import org.apache.commons.lang.StringUtils;
import org.reserved.exception.InitConfigException;
import org.reserved.exception.ParameterException;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenxuning
 */
@Component(RegexConstant.PROPERTY_BEAN_NAME)
public class PropertyUtil {

    private String nginxExe;

    private Long scheduleRefreshTime = 10 * 1000L;

    private Integer refreshUpstreamThreadCount = 12;

    private String nacosServerAddr;

    private String nginxConfigPath;

    private String nacosNamespace = "public";

    private String nacosClusterName = "DEFAULT";

    private List<ServiceDetailPojo> services;

    public class ServiceDetailPojo {
        private String nginxUpstreamValue;
        private String nacosServiceName;
        private String nacosGroupName = "DEFAULT_GROUP";

        @Override
        public String toString() {
            return "ServiceDetailPojo{" +
                    "nginxUpstreamValue='" + nginxUpstreamValue + '\'' +
                    ", nacosServiceName='" + nacosServiceName + '\'' +
                    ", nacosGroupName='" + nacosGroupName + '\'' +
                    '}';
        }

        public String getNginxUpstreamValue() {
            return nginxUpstreamValue;
        }

        public void setNginxUpstreamValue(String nginxUpstreamValue) {
            this.nginxUpstreamValue = nginxUpstreamValue;
        }

        public String getNacosServiceName() {
            return nacosServiceName;
        }

        public void setNacosServiceName(String nacosServiceName) {
            this.nacosServiceName = nacosServiceName;
        }

        public String getNacosGroupName() {
            return nacosGroupName;
        }

        public void setNacosGroupName(String nacosGroupName) {
            this.nacosGroupName = nacosGroupName;
        }
    }

    @Override
    public String toString() {
        return "PropertyUtil{" +
                "nginxExe='" + nginxExe + '\'' +
                ", scheduleRefreshTime=" + scheduleRefreshTime +
                ", refreshUpstreamThreadCount=" + refreshUpstreamThreadCount +
                ", nacosServerAddr='" + nacosServerAddr + '\'' +
                ", nginxConfigPath='" + nginxConfigPath + '\'' +
                ", nacosNamespace='" + nacosNamespace + '\'' +
                ", nacosClusterName='" + nacosClusterName + '\'' +
                ", services=" + services +
                '}';
    }

    public Integer getRefreshUpstreamThreadCount() {
        return refreshUpstreamThreadCount;
    }

    public void setRefreshUpstreamThreadCount(Integer refreshUpstreamThreadCount) {
        this.refreshUpstreamThreadCount = refreshUpstreamThreadCount;
    }

    public String getNacosClusterName() {
        return nacosClusterName;
    }

    public void setNacosClusterName(String nacosClusterName) {
        this.nacosClusterName = nacosClusterName;
    }

    public String getNginxExe() {
        return nginxExe;
    }

    public void setNginxExe(String nginxExe) {
        this.nginxExe = nginxExe;
    }

    public Long getScheduleRefreshTime() {
        return scheduleRefreshTime;
    }

    public void setScheduleRefreshTime(Long scheduleRefreshTime) {
        this.scheduleRefreshTime = scheduleRefreshTime;
    }

    public String getNacosServerAddr() {
        return nacosServerAddr;
    }

    public void setNacosServerAddr(String nacosServerAddr) {
        this.nacosServerAddr = nacosServerAddr;
    }

    public String getNacosNamespace() {
        return nacosNamespace;
    }

    public void setNacosNamespace(String nacosNamespace) {
        this.nacosNamespace = nacosNamespace;
    }

    public String getNginxConfigPath() {
        return nginxConfigPath;
    }

    public void setNginxConfigPath(String nginxConfigPath) {
        this.nginxConfigPath = nginxConfigPath;
    }

    public List<ServiceDetailPojo> getServices() {
        return services;
    }

    public void setServices(List<ServiceDetailPojo> services) {
        this.services = services;
    }

    public static List<String> nacosClusterNameToList(String nacosClusterName) throws BeansException {
        return Arrays.asList(nacosClusterName.split(","));
    }

    public static PropertyUtil verifyParameter(PropertyUtil propertiesPojo) throws BeansException {
        if (propertiesPojo == null) {
            throw new ParameterException("The config.properties is empty");
        }
        if (StringUtils.isBlank(propertiesPojo.getNginxExe())) {
            throw new ParameterException("Can not fine the parameter nginxExe");
        }
        if (StringUtils.isBlank(propertiesPojo.getNginxConfigPath())) {
            throw new ParameterException("Can not fine the parameter nginxConfigPath");
        }
        if (StringUtils.isBlank(propertiesPojo.getNacosServerAddr())) {
            throw new ParameterException("Can not fine the parameter nacosServerAddr");
        }
        if (propertiesPojo.getServices() == null) {
            throw new ParameterException("Can not fine the parameter [services]");
        }
        if (propertiesPojo.getServices().size() == 0) {
            throw new ParameterException("The parameter [services] must contain values");
        }

        File file = new File(propertiesPojo.getNginxConfigPath());
        if (!file.exists() || !file.isFile()) {
            throw new ParameterException("The path [" + propertiesPojo.getNginxConfigPath() + "] is not exist or it is not a file");
        }

        int i = -1;
        try {
            Process process = Runtime.getRuntime().exec(propertiesPojo.getNginxExe() + " -V");
            i = process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new InitConfigException("Errors occur when testing the [" + propertiesPojo.getNginxExe() + " -V" + "] command");
        }
        if (i != 0) {
            throw new InitConfigException("Errors occur when testing the [" + propertiesPojo.getNginxExe() + " -V" + "] command");
        }
        return propertiesPojo;
    }
}

