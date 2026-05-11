package kg.home.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorsConfig {

    @Bean(name = "recurringChargeExecutor")
    public ThreadPoolTaskExecutor  recurringChargeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(0);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(0);
        executor.setKeepAliveSeconds(0);
        executor.setThreadNamePrefix("rc_executor_thread");
        executor.initialize();
        return executor;
    }

}
