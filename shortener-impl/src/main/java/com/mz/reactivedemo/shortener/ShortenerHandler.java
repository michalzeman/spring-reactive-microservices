package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.common.http.HttpHandler;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.view.ShortenerQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Created by zemi on 27/09/2018.
 */
@Component
public class ShortenerHandler implements HttpHandler {

  private static final Log log = LogFactory.getLog(ShortenerHandler.class);

  private final ShortenerService shortenerService;

  private final ShortenerQuery shortenerQuery;

  public ShortenerHandler(ShortenerService shortenerService, ShortenerQuery shortenerQuery) {
    this.shortenerService = shortenerService;
    this.shortenerQuery = shortenerQuery;
  }

  Mono<ServerResponse> tick(ServerRequest req) {
    log.info("tick() ->");
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just("Tick"), String.class);
  }

  Mono<ServerResponse> map(ServerRequest request) {
    log.info("map() -> key:" + request.pathVariable("key"));
    return shortenerQuery.map(request.pathVariable("key"))
        .flatMap(url -> ServerResponse.status(HttpStatus.SEE_OTHER)
            .headers(headers -> headers.setLocation(URI
                .create(url))).build());
  }

  Mono<ServerResponse> getById(ServerRequest request) {
    log.info("getById() -> ");
    return shortenerQuery.get(request.pathVariable("eventId"))
        .flatMap(this::mapToResponse);
  }

  Mono<ServerResponse> getAll(ServerRequest request) {
    log.info("getAll() -> ");
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(shortenerQuery.getAll(), ShortenerDto.class);
  }

  Mono<ServerResponse> create(ServerRequest request) {
    log.info("execute() -> ");
    return request.bodyToMono(CreateShortener.class)
        .flatMap(shortenerService::create)
        .flatMap(this::mapToResponse);
  }

  Mono<ServerResponse> update(ServerRequest request) {
    log.info("update() -> ");
    return Mono.fromCallable(() -> request.pathVariable("eventId"))
        .publishOn(Schedulers.parallel())
        .flatMap(id -> request.bodyToMono(UpdateShortener.class)
            .flatMap(shortenerService::update)
            .flatMap(this::mapToResponse));
  }

  Mono<ServerResponse> getError(ServerRequest request) {
    log.info("getError() -> ");
    return Mono.error(new RuntimeException("Error")).flatMap(r -> ServerResponse.ok()
        .contentType(MediaType
            .APPLICATION_JSON).body(fromValue(r)));
  }

  @Override
  public RouterFunction<ServerResponse> route() {
    return RouterFunctions
        .route(GET("/").and(accept(MediaType.APPLICATION_JSON)), this::getAll)
        .andRoute(GET("/errors").and(accept(MediaType.APPLICATION_JSON)), this::getError)
        .andRoute(POST("").and(accept(MediaType.APPLICATION_JSON)), this::create)
        .andRoute(PUT("/{eventId}").and(accept(MediaType.APPLICATION_JSON)), this::update)
        .andRoute(GET("/{eventId}").and(accept(MediaType.APPLICATION_JSON)), this::getById)
        .andRoute(GET("/map/{key}")
            .and(accept(MediaType.APPLICATION_JSON)), this::map)
        .andRoute(GET("/health/ticks")
            .and(accept(MediaType.APPLICATION_JSON)), this::tick);
  }
}
