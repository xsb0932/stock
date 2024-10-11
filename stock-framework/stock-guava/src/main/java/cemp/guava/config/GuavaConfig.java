package cemp.guava.config;

import cemp.redis.util.RedisUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuavaConfig {

    @Autowired
    RedisUtils redisUtils;

    @Bean
    public LoadingCache guavaHoliday() {
        LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
                //设置并发级别为 8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(8)
                // 初始化缓存容量
                .initialCapacity(10)
                // 最大缓存容量，超出就淘汰 —— 基于容量进行回收
                .maximumSize(100L)
                // 是否需要统计缓存情况,该操作消耗一定的性能,生产环境应该去除
                .recordStats()
                // 设置缓存过期时间【写入缓存后多久过期】，超过这个时间就淘汰 —— 基于时间进行回收
                //.expireAfterWrite(10L, TimeUnit.SECONDS)
                // 设置缓存刷新时间【写入缓存后多久刷新缓存】，超过这个时间就刷新缓存，并调用refresh方法，默认是异步刷新
                //.refreshAfterWrite(5L, TimeUnit.SECONDS)
                // key 使用弱引用 WeakReference
                .weakKeys()
                // 当 Entry 被移除时的监听器
                .removalListener(notification -> System.out.println("notification=" + notification))
                // 创建一个 CacheLoader，重写 load() 方法，以实现"当 get() 时缓存不存在，则调用 load() 方法，放到缓存并返回"的效果
                // CacheLoader/load 方法是线程安全 并且阻塞的方法，这样保证只有一个线程可以访问并加锁，这样就不会有大量的请求去穿透到后端造成雪崩
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) throws Exception {
                        //从 redis中补数据
                        Object result =  redisUtils.sget("stock:holiday");
                        return result;
                    }

//                    // 异步刷新缓存
//                    @Override
//                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
//                        return super.reload(key, oldValue);
//                    }
                });

        return cache;
    }
}
