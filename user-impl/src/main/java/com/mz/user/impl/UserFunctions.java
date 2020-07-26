package com.mz.user.impl;

import com.mz.reactivedemo.common.api.events.DomainEvent;
import com.mz.reactivedemo.common.util.Match;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.domain.event.ContactInfoCreated;
import com.mz.user.domain.event.ShortenerAdded;
import com.mz.user.domain.event.UserCreated;
import com.mz.user.dto.UserDto;
import com.mz.user.message.UserPayload;
import com.mz.user.message.event.UserChangedEvent;
import com.mz.user.message.event.UserEventType;
import com.mz.user.view.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.mz.user.UserMapper.FN;

public final class UserFunctions {

  private UserFunctions() {}

  @Component
  public static class PublishUserChangedEvent implements Consumer<DomainEvent> {

    private final UserApplicationMessageBus messageBus;

    public PublishUserChangedEvent(UserApplicationMessageBus messageBus) {
      this.messageBus = messageBus;
    }

    @Override
    public void accept(DomainEvent event) {
      Match.<UserChangedEvent>match(event)
          .when(UserCreated.class, e -> UserChangedEvent.builder()
              .aggregateId(e.aggregateId())
              .payload(FN.mapCreatedToPayload.apply(e))
              .type(UserEventType.USER_CREATED)
              .build())
          .when(ContactInfoCreated.class, e -> UserChangedEvent.builder()
              .type(UserEventType.CONTACT_INFO_CREATED)
              .aggregateId(e.aggregateId())
              .payload(UserPayload.builder()
                  .id(e.aggregateId())
                  .version(e.userVersion())
                  .createdAt(e.createdAt())
                  .contactInfo(FN.mapContactCreatedToPayload.apply(e))
                  .build())
              .build())
          .when(ShortenerAdded.class, e -> UserChangedEvent.builder()
              .type(UserEventType.USER_UPDATED)
              .payload(UserPayload.builder()
                  .id(e.aggregateId())
                  .shortenerId(e.shortenerId())
                  .version(e.userVersion())
                  .build())
              .build()
          )
          .get().ifPresent(messageBus::publishEvent);
    }
  }

  @Component
  public static class UpdateUserView implements Function<UserDto, Mono<UserDto>> {

    private final UserRepository repository;

    public UpdateUserView(UserRepository repository) {
      this.repository = repository;
    }

    @Override
    public Mono<UserDto> apply(UserDto userDto) {
      return repository.save(FN.mapToDocument.apply(userDto)).map(FN.mapToDto);
    }
  }

  @Component
  public static class PublishUserDocumentMessage implements Consumer<UserDto> {

    private final UserApplicationMessageBus messageBus;

    public PublishUserDocumentMessage(UserApplicationMessageBus messageBus) {
      this.messageBus = messageBus;
    }

    @Override
    public void accept(UserDto userDto) {
      this.messageBus.publishDocumentMessage(userDto);
    }
  }
}
