package cemp.config;

import cemp.extension.StockEasySqlInjector;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    /**
     * mybatis plus 批量插入的方法
     * https://blog.csdn.net/qq_45525848/article/details/137725872
     *
     * @return
     */
    @Bean
    public StockEasySqlInjector sqlInjector() {
        return new StockEasySqlInjector();
    }

    /**
     * https://www.cnblogs.com/Retired-lad/p/17505530.html
     * mybatis plus selectPage 分页插件
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
