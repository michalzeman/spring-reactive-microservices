package com.mz.user.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.util.Logger;
import com.mz.reactivedemo.common.util.Match;
import com.mz.user.UserApplicationService;
import com.mz.user.domain.command.AddShortener;
import com.mz.user.dto.UserDto;
import com.mz.user.message.command.CreateContactInfo;
import com.mz.user.message.command.CreateUser;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class UserApplicationServiceImpl
    implements UserApplicationService {

  private Logger logger = new Logger(LogFactory.getLog(UserApplicationServiceImpl.class));

  private final AggregateService<UserDto> aggregateService;

  public UserApplicationServiceImpl(AggregateService<UserDto> aggregateService) {
    this.aggregateService = aggregateService;
  }

  @Override
  public Mono<UserDto> execute(Command cmd) {
    logger.debug(() -> "execute() ->");
    return Match.<Mono<UserDto>>match(cmd)
        .when(AddShortener.class, c -> aggregateService.execute(c.userId(), c))
        .when(CreateUser.class, this::createUser)
        .orElseGet(() -> Mono.empty());
  }

  @Override
  public Mono<UserDto> createUser(CreateUser command) {
    logger.debug(() -> "createUser() ->");
    return aggregateService.execute(UUID.randomUUID().toString(), command);
  }

  @Override
  public Mono<UserDto> createContactInfo(String userId, CreateContactInfo command) {
    logger.debug(() -> "createContactInfo() ->");
    return aggregateService.execute(userId, command);
  }

}
