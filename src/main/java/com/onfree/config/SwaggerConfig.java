package com.onfree.config;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.annotation.CurrentNormalUser;
import com.onfree.common.annotation.LoginUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    Docket docket(){
        return new Docket(DocumentationType. OAS_30)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                .build()
                .pathMapping("/")
                .apiInfo(
                        apiInfo()
                )
                .securitySchemes(
                        Arrays.asList(
                                httpAuthenticationBasicScheme(),
                                httpAuthenticationJWTScheme()

                        )
                )
                .securityContexts(
                        singletonList(
                                securityContext()
                        )
                )
                .ignoredParameterTypes(CurrentArtistUser.class, CurrentNormalUser.class, LoginUser.class)
                ;
    }



    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("onfree Rest API Doc")
                .description("온프리 API DOC 입니다.")
                .version("1.0.0")
                .termsOfServiceUrl("termsOfServiceUrl")
                .contact(new Contact("Joon", "https://github.com/wnstlr0615/onfree", "ryan0@kakao.com") )
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();

    }

    private HttpAuthenticationScheme httpAuthenticationJWTScheme() {
        return HttpAuthenticationScheme.JWT_BEARER_BUILDER
                .name("AccessToken")
                .description("JWT 토큰 인증 방식")
                .build();
    }
    private HttpAuthenticationScheme httpAuthenticationBasicScheme() {
        return HttpAuthenticationScheme.BASIC_AUTH_BUILDER
                .name("Basic")
                .description("Basic 인증 방식")
                .build();
    }
    private SecurityContext securityContext() {
        return new SecurityContextBuilder()
                .securityReferences(
                        defaultAuth()
                )
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes =
                List.of(new AuthorizationScope("global", "accessEverything"))
                        .toArray(new AuthorizationScope[0]);
        return Arrays.asList(
                new SecurityReference("AccessToken", authorizationScopes),
                new SecurityReference("Basic", authorizationScopes)
        );
    }

}
