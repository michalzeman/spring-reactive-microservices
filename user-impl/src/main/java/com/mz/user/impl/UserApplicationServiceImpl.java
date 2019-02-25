package com.mz.user.impl;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.util.Match;
import com.mz.reactivedemo.common.services.AbstractApplicationService;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.UserApplicationService;
import com.mz.user.UserFunctions;
import com.mz.user.UserRepository;
import com.mz.user.domain.aggregate.UserAggregate;
import com.mz.user.domain.aggregate.UserState;
import com.mz.user.domain.events.ContactInfoCreated;
import com.mz.user.domain.events.UserCreated;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.UserPayload;
import com.mz.user.messages.commands.CreateContactInfo;
import com.mz.user.messages.commands.CreateUser;
import com.mz.user.messages.events.UserChangedEvent;
import com.mz.user.messages.events.UserEventType;
import com.mz.user.model.UserDocument;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class UserApplicationServiceImpl extends AbstractApplicationService<UserDto, UserState, UserChangedEvent>
    implements UserApplicationService, UserFunctions {

  private final UserRepository repository;

  private final UserApplicationMessageBus messageBus;

  public UserApplicationServiceImpl(UserRepository repository, UserApplicationMessageBus messageBus) {
    this.repository = repository;
    this.messageBus = messageBus;
  }

  @Override
  protected Optional<UserChangedEvent> mapToChangedEvent(Event event, UserDto dto) {
    return Match.<UserChangedEvent>match(event)
        .when(UserCreated.class, e -> UserChangedEvent.builder()
            .payload(mapCreatedToPayload.apply(e, dto))
            .type(UserEventType.USER_CREATED)
            .build())
        .when(ContactInfoCreated.class, e -> UserChangedEvent.builder() //TODO: this is wrong
            .type(UserEventType.CONTACT_INFO_CREATED)
            .payload(UserPayload.builder()
                .id(dto.id())
                .version(dto.version())
                .createdAt(dto.createdAt())
                .contactInfo(mapContactCreatedToPlayload.apply(e))
                .build())
            .build())
        .get();
  }

  @Override
  protected void publishChangedEvent(Event event) {
    this.messageBus.publishEvent(event);
  }

  @Override
  protected void publishDocumentMessage(UserDto doc) {
    this.messageBus.publishDocumentMessage(doc);
  }

  @Override
  protected Mono<UserDto> applyToStorage(ApplyResult<UserState> result) {
    Optional<Mono<UserDocument>> dd = result.event().flatMap(event ->
        Match.<Mono<UserDocument>>match(event)
            .when(UserCreated.class, e -> repository.save(mapCreatedToDocument.apply(e)))
            .when(ContactInfoCreated.class, e -> repository.findById(e.userId())
                .flatMap(d -> {
                  d.setContactInformationDocument(mapContInfoCreatedToDoc.apply(e));
                  d.setVersion(e.userVersion().orElse(null));
                  return repository.save(d);
                })
            ).get());

    return dd.isPresent() ? dd.get().map(mapToDto) : Mono.empty();
  }

  @Override
  public Mono<UserDto> createUser(CreateUser command) {
    return processChanges(Mono.just(command)
        .map(cmd -> UserAggregate.of().apply(cmd)));
  }

  @Override
  public Mono<UserDto> createContactInfo(String userId, CreateContactInfo command) {
    return processChanges(repository.findById(userId)
        .map(mapToDto)
        .map(dto -> UserAggregate.of(dto).apply(command)));
  }
}
