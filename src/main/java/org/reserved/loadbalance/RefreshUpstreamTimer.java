package org.reserved.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenxuning
 */
@Component
public class RefreshUpstreamTimer {
    private static final Logger logger = LoggerFactory.getLogger(RefreshUpstreamTimer.class);

    private PropertyUtil propertyUtil;

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public RefreshUpstreamTimer(PropertyUtil propertyUtil) {
        this.propertyUtil = propertyUtil;
        scheduledThreadPoolExecutor =
                new ScheduledThreadPoolExecutor(propertyUtil.getRefreshUpstreamThreadCount(),
                        new ThreadPoolExecutor.CallerRunsPolicy());
        this.scheduleRefreshUpstream();
    }

    public void onceRefreshUpstream() {
        scheduledThreadPoolExecutor.execute(new RefreshUpstreamTask());
    }

    private void scheduleRefreshUpstream() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new RefreshUpstreamTask(), propertyUtil.getScheduleRefreshTime(), propertyUtil.getScheduleRefreshTime(), TimeUnit.MILLISECONDS);
    }

    private class RefreshUpstreamTask implements Runnable {
        @Override
        public void run() {
            try {
                String nginxExe = propertyUtil.getNginxExe();
                Process process = Runtime.getRuntime().exec(nginxExe + " -t");
                boolean result = process.waitFor(5, TimeUnit.SECONDS);
                if (!result) {
                    logger.error("The [{}] command dose not give the response", (nginxExe + " -t"));
                    return;
                }
                if (process.exitValue() != 0) {
                    logger.error("Errors occur in the [{}] command,you can execute it to see the detail", (nginxExe + " -t"));
                    return;
                }
                process = Runtime.getRuntime().exec(nginxExe + " -s reload");
                result = process.waitFor(5, TimeUnit.SECONDS);
                if (!result) {
                    logger.error("The [{}] command dose not give the response", (nginxExe + " -t"));
                    return;
                }
                if (process.exitValue() != 0) {
                    logger.error("Errors occur in the [{}] command,you can execute it to see the detail", (nginxExe + " -s reload"));
                }
            } catch (Exception e) {
                logger.error("Errors occur when refreshing the upstream", e);
            }
        }
    }


}
