package com.mz.user;

import com.mz.user.dto.UserDto;
import com.mz.user.messages.commands.CreateContactInfo;
import com.mz.user.messages.commands.CreateUser;
import reactor.core.publisher.Mono;

public interface UserApplicationService {
  Mono<UserDto> createUser(CreateUser command);
  Mono<UserDto> createContactInfo(String userId, CreateContactInfo command);
}
