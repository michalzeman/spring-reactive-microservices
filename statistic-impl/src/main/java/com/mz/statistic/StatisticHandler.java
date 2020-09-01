package com.mz.statistic;

import com.mz.reactivedemo.common.http.HttpHandler;
import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
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
public class StatisticHandler implements HttpHandler {

  private final StatisticService service;

  public StatisticHandler(StatisticService service) {
    this.service = service;
  }


  Mono<ServerResponse> getAll(ServerRequest request) {
    return ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.getAll(), StatisticDocument.class);
  }

  Mono<ServerResponse> eventsCount(ServerRequest request) {
    return Mono.just(EventType.valueOf(request.pathVariable("type")))
        .flatMap(t -> ok().contentType(MediaType.APPLICATION_JSON)
            .body(service.eventsCount(t), Long.class));
  }

  @Override
  public RouterFunction<ServerResponse> route() {
    return RouterFunctions
        .route(GET("/").and(accept(MediaType.APPLICATION_JSON)), this::getAll)
        .andRoute(GET("/shorteners/events/{type}/counts").and(accept(MediaType.APPLICATION_JSON)),
            this::eventsCount);
  }
}
