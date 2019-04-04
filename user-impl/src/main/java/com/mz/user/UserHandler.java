package com.mz.user;

import com.mz.reactivedemo.common.errors.ErrorHandler;
import com.mz.user.dto.UserDto;
import com.mz.user.message.command.CreateContactInfo;
import com.mz.user.message.command.CreateUser;
import com.mz.user.view.UserQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class UserHandler implements ErrorHandler {

  private static final Log log = LogFactory.getLog(UserHandler.class);

  private final UserApplicationService userApplicationService;

  private final UserQuery userQuery;

  public UserHandler(UserApplicationService userApplicationService, UserQuery userQuery) {
    this.userApplicationService = userApplicationService;
    this.userQuery = userQuery;
  }

  private void logOnError(Throwable e) {
    log.error(e);
  }

  Mono<ServerResponse> createUser(ServerRequest request) {
    log.info("createUser() -> ");
    return request.bodyToMono(CreateUser.class)
        .flatMap(userApplicationService::createUser)
        .flatMap(r -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(r)))
        .doOnError(this::logOnError)
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> createContactInfo(ServerRequest request) {
    log.info("createContactInfo() -> ");
    return Mono.just(request.pathVariable("userId"))
        .flatMap(userId -> request.bodyToMono(CreateContactInfo.class)
            .flatMap(cmd -> userApplicationService.createContactInfo(userId, cmd)))
        .flatMap(r -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(r)))
        .doOnError(this::logOnError)
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> getAll(ServerRequest request) {
    log.info("getAll() -> ");
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(userQuery.getAll(), UserDto.class)
        .doOnError(this::logOnError)
        .onErrorResume(this::onError);
  }

  Mono<ServerResponse> getById(ServerRequest request) {
    log.info("getById() -> ");
    return userQuery.getById(request.pathVariable("id"))
        .flatMap(r -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8).body(fromObject(r)))
        .doOnError(this::logOnError)
        .onErrorResume(this::onError);
  }

  @Configuration
  public static class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
      return RouterFunctions.nest(RequestPredicates.path("/users"),
          RouterFunctions
              .route(POST("").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::createUser)
              .andRoute(PUT("/{userId}/contactinformation").and(accept(MediaType.APPLICATION_JSON_UTF8)),
                  handler::createContactInfo)
              .andRoute(GET("").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::getAll)
              .andRoute(GET("/{id}").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::getById)
      );
    }

  }
}
