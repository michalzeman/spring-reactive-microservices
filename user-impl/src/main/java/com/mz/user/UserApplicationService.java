package com.mz.user;

import com.mz.user.dto.UserDto;
import com.mz.user.messages.CreateUser;
import reactor.core.publisher.Mono;

public interface UserApplicationService {
  Mono<UserDto> createUser(CreateUser command);
}
