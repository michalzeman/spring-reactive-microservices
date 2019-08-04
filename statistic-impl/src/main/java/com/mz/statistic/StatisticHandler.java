package com.mz.statistic;

import com.mz.reactivedemo.common.errors.ErrorHandler;
import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


/**
 * Created by zemi on 26/09/2018.
 */
@Component
public class StatisticHandler implements ErrorHandler {

  private final StatisticService service;

  public StatisticHandler(StatisticService service) {
    this.service = service;
  }


  Mono<ServerResponse> getAll(ServerRequest request) {
    return ok()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(service.getAll(), StatisticDocument.class)
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> eventsCount(ServerRequest request) {
    return Mono.just(EventType.valueOf(request.pathVariable("type")))
        .flatMap(t -> ok().contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(service.eventsCount(t), Long.class))
        .onErrorResume(this::onError);
  }

  @Configuration
  static public class StatisticRouter {

    @Bean
    public RouterFunction<ServerResponse> statisticRoute(StatisticHandler handler) {
      System.out.println("StatisticRouter");
      return RouterFunctions
          .route(GET("/statistics").and(accept(MediaType.APPLICATION_JSON)), handler::getAll)
          .andRoute(GET("/statistics/shorteners/events/{type}/counts").and(accept(MediaType.APPLICATION_JSON_UTF8)),
              handler::eventsCount);
    }
  }

}
