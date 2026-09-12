package br.com.bemdoar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Faz a pasta ./uploads ser servida pela URL /uploads/**
 * (RF24 - evidencias das acoes sociais).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String diretorioUpload;

    public WebConfig(@Value("${bemdoar.upload.diretorio}") String diretorioUpload) {
        this.diretorioUpload = diretorioUpload;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path caminho = Paths.get(diretorioUpload).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + caminho + "/");
    }
}
