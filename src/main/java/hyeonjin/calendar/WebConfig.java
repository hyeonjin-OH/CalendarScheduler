package hyeonjin.calendar;


import hyeonjin.calendar.domain.member.PwdEncrypt;
import hyeonjin.calendar.web.argumentresolver.LoginMemberArgumentResolver;
import hyeonjin.calendar.web.interceptor.LogInterceptor;
import hyeonjin.calendar.web.interceptor.LoginCheckInterceptor;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    //private final JwtProvider jwtProvider;
    //private final JwtInterceptor jwtInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/images/**", "/*.ico", "/error","/js/**");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/join","/login","/logout","/join/**","/CalendarCategory","/CalendarCategory/**","/login/**"
                         ,"/css/**", "/images/**", "/*.ico", "/error","/js/**","/favicon.ico");
        /*
        registry.addInterceptor(jwtInterceptor)
                .order(3)
                .addPathPatterns("/**")
                .excludePathPatterns("/","/login","/css/**", "/images/**", "/*.ico", "/error", "/login/**");


         */
    }
/*
    @Bean
    public FilterRegistrationBean logFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean loginCheckFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

 */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);
    }
    @Bean
    public WebClient webClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public PwdEncrypt makePwdEncrpyt(){
        return new PwdEncrypt();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/favicon.ico",
                "/error",
                "/css/**",
                "/images/**",
                "/js/**"
        );
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return new NullAuthoritiesMapper();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // 권한 체크 하지 않을 urRl
        http.authorizeRequests()
                .requestMatchers("/", "/join","/login","/join/**","/login/**","/**").permitAll()
                .anyRequest().authenticated();

        //http.addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


}
