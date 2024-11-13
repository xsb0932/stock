package cemp.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class StockThreadPoolConf {


    @Bean
    public ThreadPoolTaskExecutor sendMailPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);           //核心线程数 再队列没有满的情况下 当前线程会先增加到corepoolsize 来处理业务
        executor.setMaxPoolSize(20);            //最大线程数
        executor.setQueueCapacity(20);         //队列最大容量，超过容量那么线程数就从corepoolsize -> maxpoolsize
        executor.setKeepAliveSeconds(20);      //空闲时间 如果线程没有被使用超过了live time 那么线程就被标记为闲置 线程 并且会回收 - 当前线程数 -> corepoolsize
        executor.setThreadNamePrefix("device-kanban-thread");
        //当 queue满了 线程达到了maxpoolsize 那么整个线程池就满了 就需要根据拒绝策略 处理溢出的请求
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor baseStockExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setKeepAliveSeconds(100);
        executor.setThreadNamePrefix("web-kanban-thread");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
