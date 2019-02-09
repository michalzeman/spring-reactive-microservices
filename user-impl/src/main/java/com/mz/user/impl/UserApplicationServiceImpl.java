package com.mz.user.impl;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.services.AbstractApplicationService;
import com.mz.user.UserApplicationService;
import com.mz.user.UserFunctions;
import com.mz.user.UserRepository;
import com.mz.user.domain.aggregate.UserRootEntity;
import com.mz.user.domain.events.ContactInfoCreated;
import com.mz.user.domain.events.UserChangedEvent;
import com.mz.user.domain.events.UserCreated;
import com.mz.user.domain.events.UserEventType;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.CreateUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class UserApplicationServiceImpl extends AbstractApplicationService<UserDto, UserChangedEvent>
    implements UserApplicationService, UserFunctions {

  private final UserRepository repository;

  public UserApplicationServiceImpl(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  protected Optional<UserChangedEvent> mapToChangedEvent(Event event, UserDto dto) {
    if (casePattern(event, UserCreated.class)) {
      UserCreated userCreated = (UserCreated)event;
      return Optional.of(UserChangedEvent.builder()
          .payload(UserDto.builder().from(userCreated.user())
              .id(dto.id())
              .version(dto.version())
              .build())
          .type(UserEventType.USER_CREATED)
          .build());
    } else if (casePattern(event, ContactInfoCreated.class)) {
      return Optional.of(UserChangedEvent.builder()
          .type(UserEventType.CONTACT_INFO_CREATED)
          .payload(UserDto.builder()
              .contactInformation(dto.contactInformation())
              .build())
          .build()
      );
    }
    return Optional.empty();
  }

  @Override
  protected void publishChangedEvent(Event event) {

  }

  @Override
  protected void publishDocumentMessage(UserDto doc) {

  }

  @Override
  protected Mono<UserDto> applyToStorage(ApplyResult<UserDto> result) {
    return repository.save(mapToDocument.apply(result.result()))
        .map(mapToDto);
  }

  @Override
  public Mono<UserDto> createUser(CreateUser command) {
    return processChanges(Mono.just(command)
        .map(cmd -> UserRootEntity.of().apply(cmd)));
  }
}
