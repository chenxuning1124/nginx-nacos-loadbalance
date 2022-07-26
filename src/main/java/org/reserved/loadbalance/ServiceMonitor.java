package org.reserved.loadbalance;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenxuning
 */
@Component
public class ServiceMonitor {

    private RefreshUpstreamTimer refreshUpstreamTimer;
    private PropertyUtil propertyUtil;
    private NamingService initNamingService;

    private static final Logger logger = LoggerFactory.getLogger(ServiceMonitor.class);

    @Autowired
    public ServiceMonitor(PropertyUtil propertyUtil, NamingService initNamingService, RefreshUpstreamTimer refreshUpstreamTimer) {
        this.propertyUtil = propertyUtil;
        this.initNamingService = initNamingService;
        this.refreshUpstreamTimer = refreshUpstreamTimer;
        this.monitor();
    }

    private void monitor() {
        List<PropertyUtil.ServiceDetailPojo> services = propertyUtil.getServices();
        for (PropertyUtil.ServiceDetailPojo serviceDetail : services) {
            try {
                initNamingService.subscribe(
                        serviceDetail.getNacosServiceName(),
                        serviceDetail.getNacosGroupName(),
                        PropertyUtil.nacosClusterNameToList(propertyUtil.getNacosClusterName()),
                        event -> {
                            if (event instanceof NamingEvent) {
                                List<Instance> instances = ((NamingEvent) event).getInstances();
                                if (refreshUpstream(instances, serviceDetail.getNginxUpstreamValue(), propertyUtil.getNginxConfigPath())) {
                                    refreshUpstreamTimer.onceRefreshUpstream();
                                }
                            }
                        }
                );
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean refreshUpstream(List<Instance> instances, String nginxUpstream, String nginxConfigPath) {
        File file = new File(nginxConfigPath);
        if (!file.exists() || !file.isFile()) {
            logger.error("file :{} is not exist or it is not a file", nginxConfigPath);
            return false;
        }
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(bytes);
        } catch (IOException e) {
            logger.error("Errors occur when loading the nginx config:{}", e.getMessage());
            return false;
        }
        String conf = new String(bytes);

        Pattern pattern = Pattern.compile(RegexConstant.UPSTREAM_REGEX.replace(RegexConstant.UPSTREAM_NAME, nginxUpstream));
        Matcher matcher = pattern.matcher(conf);
        if (matcher.find()) {
            StringBuilder tabSpace = new StringBuilder();
            String oldUpstream = matcher.group();
            int index = conf.indexOf(oldUpstream);
            while (index != 0 && (conf.charAt(index - 1) == ' ' || conf.charAt(index - 1) == '\t')) {
                tabSpace.append(conf.charAt(index - 1));
                index--;
            }
            String newUpstream = RegexConstant.UPSTREAM_FORMAT.replace(RegexConstant.UPSTREAM_NAME, nginxUpstream);
            String serverFormat;
            if ((serverFormat = this.getServerFormat(instances)).length() != 0) {
                serverFormat = serverFormat + tabSpace;
                newUpstream = newUpstream.replace(RegexConstant.PLACEHOLDER_SERVER, serverFormat);
                if (oldUpstream.equals(newUpstream)) {
                    return false;
                }
                logger.info("【update server】:\n{}", newUpstream);
                conf = matcher.replaceAll(newUpstream);
            } else {
                Pattern patternDelete = Pattern.compile(RegexConstant.UPSTREAM_REGEX.replace(RegexConstant.UPSTREAM_NAME, nginxUpstream) + "\n");
                Matcher matcherDelete = patternDelete.matcher(conf);
                logger.info("【delete upstream】:{}", nginxUpstream);
                conf = matcherDelete.replaceAll("");
            }
        } else {
            String newUpstream = RegexConstant.UPSTREAM_FORMAT.replace(RegexConstant.UPSTREAM_NAME, nginxUpstream);
            String serverFormat;
            if ((serverFormat = this.getServerFormat(instances)).length() != 0) {
                newUpstream = newUpstream.replace(RegexConstant.PLACEHOLDER_SERVER, serverFormat);
                logger.info("【create upstream】:\n{}", newUpstream);
                conf = newUpstream + "\n" + conf;
            } else {
                return false;
            }
        }
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(conf);
            fileWriter.flush();
        } catch (IOException e) {
            logger.error("Errors occur when writing the nginx config:{}", e.getMessage());
            return false;
        }
        return true;
    }

    private String getServerFormat(List<Instance> instanceList) {
        StringBuilder servers = new StringBuilder();
        if (instanceList.size() > 0) {
            for (Instance instance : instanceList) {
                if (!instance.isHealthy() || !instance.isEnabled()) {
                    continue;
                }
                String ip = instance.getIp();
                int port = instance.getPort();
                servers.append("    server ").append(ip).append(":").append(port).append(";\n");
            }
        }
        return servers.toString();
    }
}
