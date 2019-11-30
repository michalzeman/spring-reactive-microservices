package com.mz.statistic;

import com.mz.reactivedemo.common.http.HttpErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class StatisticConfiguration {

  @Bean
  public RouterFunction<ServerResponse> statisticRoute(StatisticHandler handler) {
    return RouterFunctions.route()
        .add(RouterFunctions.nest(RequestPredicates.path("/statistics"),handler.route()))
        .onError(Throwable.class, HttpErrorHandler.FN::onError)
        .build();
  }

}
