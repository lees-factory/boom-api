package io.lees.boom.core.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val userArgumentResolver: UserArgumentResolver,
) : WebMvcConfigurer {
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
