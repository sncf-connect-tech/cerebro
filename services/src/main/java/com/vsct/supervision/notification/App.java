/*
 * This file is part of the Cerebro distribution.
 * (https://github.com/voyages-sncf-technologies/cerebro)
 * Copyright (C) 2017 VSCT.
 *
 * Cerebro is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, version 3 of the License.
 *
 * Cerebro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.vsct.supervision.notification;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.vsct.supervision.config.CerebroConfiguration;
import com.vsct.supervision.notification.email.Sender;
import com.vsct.supervision.notification.email.MailSenderImpl;
import com.vsct.supervision.notification.exception.SeyrenResponseErrorHandler;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableConfigurationProperties
@EnableSwagger2
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
            .apis(RequestHandlerSelectors.basePackage("com.vsct.supervision")).paths(PathSelectors.any()).build()
            .useDefaultResponseMessages(false).globalResponseMessage(RequestMethod.GET, responseMessageList())
            .globalResponseMessage(RequestMethod.DELETE, responseMessageList())
            .globalResponseMessage(RequestMethod.POST, responseMessageList())
            .globalResponseMessage(RequestMethod.PUT, responseMessageList());
    }

    private List<ResponseMessage> responseMessageList() {
        return newArrayList(new ResponseMessageBuilder().code(500).message("Server Error").responseModel(new ModelRef("Error")).build(),
            new ResponseMessageBuilder().code(401).message("Unauthorized").build());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Cerebro Services API").description("Cerebro is an open alerting system for DevOps teams.").license("GNU Affero General Public License")
            .licenseUrl("http://www.gnu.org/licenses/agpl-3.0.txt").contact(new Contact("VSCT", "https://github.com/voyages-sncf-technologies", "cerebro-team(at)framalistes.org")).version("1.0.0-SNAPSHOT").build();
    }

    @Bean
    public FilterRegistrationBean correlationIdFilter() {
        return new FilterRegistrationBean(new Filter() {

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
            }

            @Override
            public void destroy() {
            }
        });
    }

    @Bean
    public Sender sender() {
        return new MailSenderImpl();
    }

    @Bean
    public CerebroConfiguration configuration() {
        return new CerebroConfiguration();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restClient = new RestTemplate();
        restClient.setErrorHandler(new SeyrenResponseErrorHandler());
        return restClient;
    }

    @PostConstruct
    public void logProperties() throws IOException {
        Properties appProperties = new Properties();
        appProperties.load(App.class.getResourceAsStream("/config/application.properties"));
    }

    public static void main(final String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.run(args);
    }
}
