package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.common.errors.ErrorHandler;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Created by zemi on 27/09/2018.
 */
@Component
public class ShortenerHandler implements ErrorHandler {

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
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(Mono.just("Tick"), String.class)
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> map(ServerRequest request) {
    log.info("map() -> key:" + request.pathVariable("key"));
    return shortenerQuery.map(request.pathVariable("key"))
        .flatMap(url -> ServerResponse.status(HttpStatus.SEE_OTHER)
            .headers(headers -> headers.setLocation(URI
                .create(url))).build())
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> getById(ServerRequest request) {
    log.info("getById() -> ");
    return shortenerQuery.get(request.pathVariable("id"))
        .flatMap(r -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(r)))
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> getAll(ServerRequest request) {
    log.info("getAll() -> ");
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(shortenerQuery.getAll(), ShortenerDto.class)
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> create(ServerRequest request) {
    log.info("create() -> ");
    return request.bodyToMono(CreateShortener.class)
        .flatMap(createShortener -> shortenerService.create(createShortener))
        .flatMap(r -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(r)))
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> update(ServerRequest request) {
    log.info("update() -> ");
    return Mono.just(request.pathVariable("id"))
        .publishOn(Schedulers.parallel())
        .flatMap(id -> request.bodyToMono(UpdateShortener.class)
            .flatMap(shortenerService::update)
            .flatMap(r -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(fromObject(r))))
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> getError(ServerRequest request) {
    log.info("getError() -> ");
    return Mono.error(new RuntimeException("Error")).flatMap(r -> ServerResponse.ok()
        .contentType(MediaType
            .APPLICATION_JSON_UTF8).body(fromObject(r)))
        .onErrorResume(this::onError);
  }


  @Configuration
  static public class ShortenerRouter {

    @Bean
    public RouterFunction<ServerResponse> shortenerRoute(ShortenerHandler handler) {
      return RouterFunctions.nest(RequestPredicates.path("/shorteners"),
          RouterFunctions
              .route(GET("/").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::getAll)
              .andRoute(GET("/errors").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::getError)
              .andRoute(POST("").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::create)
              .andRoute(PUT("/{id}").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::update)
              .andRoute(GET("/{id}").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::getById)
              .andRoute(GET("/map/{key}")
                  .and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::map)
              .andRoute(GET("/health/ticks")
                  .and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::tick));

    }

  }
}
