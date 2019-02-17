package com.mz.user.impl;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.utils.PatternMatching;
import com.mz.reactivedemo.common.services.AbstractApplicationService;
import com.mz.user.UserApplicationMessageBus;
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
import com.mz.user.model.ContactInfoDocument;
import com.mz.user.model.UserDocument;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;

@Component
public class UserApplicationServiceImpl extends AbstractApplicationService<UserDto, UserChangedEvent>
    implements UserApplicationService, UserFunctions {

  private final UserRepository repository;

  private final UserApplicationMessageBus messageBus;

  public UserApplicationServiceImpl(UserRepository repository, UserApplicationMessageBus messageBus) {
    this.repository = repository;
    this.messageBus = messageBus;
  }

  @Override
  protected Optional<UserChangedEvent> mapToChangedEvent(Event event, UserDto dto) {
    return PatternMatching.<UserChangedEvent>match(event)
        .when(UserCreated.class, e -> UserChangedEvent.builder()
            .payload(dto)
            .type(UserEventType.USER_CREATED)
            .build())
        .when(ContactInfoCreated.class, e -> UserChangedEvent.builder()
            .type(UserEventType.CONTACT_INFO_CREATED)
            .payload(dto)
            .build())
        .result();
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
  protected Mono<UserDto> applyToStorage(ApplyResult<UserDto> result) {
    Optional<Mono<UserDocument>> dd = result.event().flatMap(event ->
        PatternMatching.<Mono<UserDocument>>match(event)
            .when(UserCreated.class, e -> {
              UserDocument document = new UserDocument();
              e.firstName().ifPresent(document::setFirstName);
              e.lastName().ifPresent(document::setLastName);
              mapContactInfoDoc(document, e.createdAt(), e.email(), e.phoneNumber());
              document.setVersion(e.version());
              document.setCreatedAt(e.createdAt());
              return repository.save(document);
            })
            .when(ContactInfoCreated.class, e ->
                repository.findById(e.userId())
                    .flatMap(d -> {
                      mapContactInfoDoc(d, e.createdAt(), e.email(), e.phoneNumber());
                      d.setVersion(e.userVersion());
                      return repository.save(d);
                    })
            ).result());

    return dd.isPresent() ? dd.get().map(mapToDto) : Mono.empty();
  }

  private void mapContactInfoDoc(UserDocument document, Instant instant, Optional<String> email, Optional<String> s) {
    ContactInfoDocument contactInfoDocument = new ContactInfoDocument();
    contactInfoDocument.setCreatedAt(instant);
    email.ifPresent(contactInfoDocument::setEmail);
    s.ifPresent(contactInfoDocument::setPhoneNumber);
    document.setContactInformationDocument(contactInfoDocument);
  }

  @Override
  public Mono<UserDto> createUser(CreateUser command) {
    return processChanges(Mono.just(command)
        .map(cmd -> UserRootEntity.of().apply(cmd)));
  }
}
