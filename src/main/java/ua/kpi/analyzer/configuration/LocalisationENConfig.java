package ua.kpi.analyzer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Ihor Sytnik
 */
@Configuration
@Profile("en")
public class LocalisationENConfig {

    @Bean
    public Properties locText() throws IOException {
        Resource resource = new ClassPathResource("/localisation/english.properties");
        return PropertiesLoaderUtils.loadProperties(resource);
    }

}
