package com.mz.user;

import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregatePersistenceConfiguration;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.common.http.HttpErrorHandler;
import com.mz.user.domain.aggregate.UserAggregate;
import com.mz.user.dto.UserDto;
import com.mz.user.impl.UserFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@Import(AggregatePersistenceConfiguration.class)
public class UserApplicationConfiguration {

  @Bean
  public AggregateService<UserDto> aggregateService(
      UserFunctions.UpdateUserView updateUserView,
      UserFunctions.PublishUserChangedEvent publishUserChanged,
      UserFunctions.PublishUserDocumentMessage publishDocumentMessage,
      AggregateRepository aggregateRepository) {
    return AggregateService.of(aggregateRepository,
        AggregateFactory.build(UserAggregate::of, UserAggregate::of),
        updateUserView, publishUserChanged,
        publishDocumentMessage);
  }

  @Bean
  public RouterFunction<ServerResponse> userRoute(UserHandler handler) {
    return RouterFunctions.route()
        .add(RouterFunctions.nest(RequestPredicates.path("/users"),handler.route()))
        .add(RouterFunctions.route(GET("/health/ticks")
            .and(accept(MediaType.APPLICATION_JSON_UTF8)), req -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just("Tick"), String.class))
        )
        .onError(Throwable.class, HttpErrorHandler.FN::onError)
        .build();
  }

}
