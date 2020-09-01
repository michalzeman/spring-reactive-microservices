package com.mz.user;

import com.mz.reactivedemo.common.http.HttpHandler;
import com.mz.user.dto.UserDto;
import com.mz.user.message.command.CreateContactInfo;
import com.mz.user.message.command.CreateUser;
import com.mz.user.view.UserQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class UserHandler implements HttpHandler {

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
        .flatMap(this::mapToResponse)
        .doOnError(this::logOnError);
  }

  Mono<ServerResponse> createContactInfo(ServerRequest request) {
    log.info("createContactInfo() -> ");
    return Mono.just(request.pathVariable("userId"))
        .flatMap(userId -> request.bodyToMono(CreateContactInfo.class)
            .flatMap(cmd -> userApplicationService.createContactInfo(userId, cmd)))
        .flatMap(this::mapToResponse)
        .doOnError(this::logOnError);
  }

  Mono<ServerResponse> getAll(ServerRequest request) {
    log.info("getAll() -> ");
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(userQuery.getAll(), UserDto.class)
        .doOnError(this::logOnError);
  }

  Mono<ServerResponse> getById(ServerRequest request) {
    log.info("getById() -> ");
    return userQuery.getById(request.pathVariable("id"))
        .flatMap(this::mapToResponse)
        .doOnError(this::logOnError);
  }

  public RouterFunction<ServerResponse> route() {
    return RouterFunctions
        .route(POST("").and(accept(MediaType.APPLICATION_JSON)), this::createUser)
        .andRoute(PUT("/{userId}/contactinformation").and(accept(MediaType.APPLICATION_JSON)),
            this::createContactInfo)
        .andRoute(GET("").and(accept(MediaType.APPLICATION_JSON)), this::getAll)
        .andRoute(GET("/{id}").and(accept(MediaType.APPLICATION_JSON)), this::getById);
  }
}
