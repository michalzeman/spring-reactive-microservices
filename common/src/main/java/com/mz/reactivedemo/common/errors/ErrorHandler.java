package com.mz.reactivedemo.common.errors;

import com.mz.reactivedemo.common.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * Created by zemi on 02/10/2018.
 */
public interface ErrorHandler {

  default Mono<ServerResponse> onError(Throwable e) {
    return Mono.just(new ErrorMessage(e.getMessage()))
        .flatMap(error -> ServerResponse.status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(fromObject(error)));
  }
}
