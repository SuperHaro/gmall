package online.superh.gmall.util.conf;

import online.superh.gmall.util.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-05-01 13:02
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter{
    @Autowired
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
