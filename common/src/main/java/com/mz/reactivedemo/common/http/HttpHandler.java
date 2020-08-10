package com.mz.reactivedemo.common.http;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * Created by zemi on 02/10/2018.
 */
public interface HttpHandler {

  default <T> Mono<ServerResponse> mapToResponse(T result) {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(result));
  }

  RouterFunction<ServerResponse> route();
}
