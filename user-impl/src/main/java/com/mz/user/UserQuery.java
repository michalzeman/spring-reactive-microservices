package com.mz.user;

import com.mz.user.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserQuery {

  Mono<UserDto> getById(String id);

  Flux<UserDto> getAll();
}
