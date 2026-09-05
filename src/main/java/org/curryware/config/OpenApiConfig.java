package org.curryware.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI currywareOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Curryware Fantasy Sports API")
                        .description("API serving fantasy sports data to the Next.js web app, Android app, and iOS app.")
                        .version("v1"));
    }
}
