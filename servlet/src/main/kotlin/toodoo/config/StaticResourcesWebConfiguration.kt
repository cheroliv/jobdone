package toodoo.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tech.jhipster.config.JHipsterConstants
import tech.jhipster.config.JHipsterProperties
import java.util.concurrent.TimeUnit

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
class StaticResourcesWebConfiguration(
    private val jHipsterProperties: JHipsterProperties
) : WebMvcConfigurer {

    companion object {
        val RESOURCE_LOCATIONS = arrayOf("classpath:/static/", "classpath:/static/content/", "classpath:/static/i18n/")
        val RESOURCE_PATHS = arrayOf("/*.js", "/*.css", "/*.svg", "/*.png", "*.ico", "/content/**", "/i18n/*")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val resourceHandlerRegistration = appendResourceHandler(registry)
        initializeResourceHandler(resourceHandlerRegistration)
    }

    fun appendResourceHandler(registry: ResourceHandlerRegistry) = registry.addResourceHandler(*RESOURCE_PATHS)

    fun initializeResourceHandler(resourceHandlerRegistration: ResourceHandlerRegistration) {
        resourceHandlerRegistration.addResourceLocations(*RESOURCE_LOCATIONS).setCacheControl(getCacheControl())
    }

    fun getCacheControl() = CacheControl.maxAge(getJHipsterHttpCacheProperty().toLong(), TimeUnit.DAYS).cachePublic()

    private fun getJHipsterHttpCacheProperty() = jHipsterProperties.http.cache.timeToLiveInDays
}
