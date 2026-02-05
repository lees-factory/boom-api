package io.lees.boom.core.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val userArgumentResolver: UserArgumentResolver,
    private val authInterceptor: AuthInterceptor, // [주입]
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(authInterceptor)
            .addPathPatterns("/api/**") // 기본적으로 모든 API 검사
            .excludePathPatterns(
                "/api/v1/members/login", // 로그인
                "/api/v1/members/refresh", // 토큰 갱신
                "/docs/**",
                "/health",
                "/favicon.ico",
                "/error",
            )
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(userArgumentResolver)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/docs/**")
            .addResourceLocations("classpath:/static/docs/")
        registry
            .addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/static/")
        registry
            .addResourceHandler("/js/**")
            .addResourceLocations("/js/")
        registry
            .addResourceHandler("/css/**")
            .addResourceLocations("/css/")
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("/images/")
    }
}
