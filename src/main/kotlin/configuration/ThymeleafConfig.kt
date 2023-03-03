package configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class ThymeleafConfig : WebMvcConfigurer {

    override fun configureViewResolvers(resolverRegistry: ViewResolverRegistry) {
        val resolver = SpringResourceTemplateResolver()
        resolver.prefix = "classpath:/templates/"
        resolver.suffix = ".html"
        resolver.characterEncoding = "UTF-8"
        resolver.templateMode = TemplateMode.HTML
        resolver.isCacheable = true

        val viewResolver = ThymeleafViewResolver()
        viewResolver.templateEngine = templateEngine(resolver)
        viewResolver.order = 1

        resolverRegistry.viewResolver(viewResolver)
    }

    private fun templateEngine(resolver: ITemplateResolver): SpringTemplateEngine {
        val engine = SpringTemplateEngine()
        engine.setTemplateResolver(resolver)
        return engine
    }

}