package com.mz.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by zemi on 31/10/2018.
 */
@Configuration
public class RouteConfiguration {

  final String statisticUri;

  final String shortenerUri;

  final String userUri;

  public RouteConfiguration(@Value("${service.statistic.uri}") String statisticUri,
                            @Value("${service.shortener.uri}") String shortenerUri,
                            @Value("${service.user.uri}") String userUri) {
    this.statisticUri = statisticUri;
    this.shortenerUri = shortenerUri;
    this.userUri = userUri;
  }

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("statistic_id", r -> r.path("/statistics/**")
            .uri(statisticUri)
        )
        .route("shortener_id", r -> r.path("/shorteners/**")
            .uri(shortenerUri)
        )
        .route("user_id", r -> r.path("/users/**")
            .uri(userUri)
        )
        .build();
  }

}
