package com.mz.user;

import com.mz.reactivedemo.common.errors.ErrorHandler;
import com.mz.user.messages.commands.CreateContactInfo;
import com.mz.user.messages.commands.CreateUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class UserHandler implements ErrorHandler {

  private static final Log log = LogFactory.getLog(UserHandler.class);

  private final UserApplicationService userApplicationService;

  public UserHandler(UserApplicationService userApplicationService) {
    this.userApplicationService = userApplicationService;
  }

  Mono<ServerResponse> createUser(ServerRequest request) {
    log.info("createUser() -> ");
    return request.bodyToMono(CreateUser.class)
        .flatMap(userApplicationService::createUser)
        .flatMap(r -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(r)))
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> createContactInfo(ServerRequest request) {
    log.info("createContactInfo() -> ");
    return Mono.just(request.pathVariable("userId"))
        .flatMap(userId -> request.bodyToMono(CreateContactInfo.class)
            .flatMap(cmd -> userApplicationService.createContactInfo(userId, cmd)))
        .flatMap(r -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(r)))
        .log()
        .onErrorResume(this::onError);
  }

  @Configuration
  public static class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
      return RouterFunctions.nest(RequestPredicates.path("/users"),
          RouterFunctions
              .route(POST("").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::createUser)
          .andRoute(PUT("/{userId}").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::createContactInfo)
      );
    }

  }
}
