package io.selja.seljabackend.configuration

import io.selja.seljabackend.service.UPLOADED_FOLDER
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ResourceConfiguration : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:${UPLOADED_FOLDER}")
    }
}