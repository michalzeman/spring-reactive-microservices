package com.mz.user.impl;

import com.mz.reactivedemo.adapter.persistance.persistence.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.persistence.PersistenceRepository;
import com.mz.reactivedemo.adapter.persistance.persistence.impl.AggregateFactoryImpl;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.DomainEvent;
import com.mz.reactivedemo.common.service.ApplicationService;
import com.mz.reactivedemo.common.util.Logger;
import com.mz.reactivedemo.common.util.Match;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.UserApplicationService;
import com.mz.user.UserFunctions;
import com.mz.user.domain.aggregate.UserAggregate;
import com.mz.user.domain.command.AddShortener;
import com.mz.user.domain.event.ContactInfoCreated;
import com.mz.user.domain.event.ShortenerAdded;
import com.mz.user.domain.event.UserCreated;
import com.mz.user.dto.UserDto;
import com.mz.user.message.UserPayload;
import com.mz.user.message.command.CreateContactInfo;
import com.mz.user.message.command.CreateUser;
import com.mz.user.message.event.UserChangedEvent;
import com.mz.user.message.event.UserEventType;
import com.mz.user.view.UserRepository;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class UserApplicationServiceImpl
    implements UserApplicationService, UserFunctions {

  private Logger logger = new Logger(LogFactory.getLog(UserApplicationServiceImpl.class));

  private final UserRepository repository;

  private final UserApplicationMessageBus messageBus;

  private final PersistenceRepository persistenceRepository;

  private final AggregateFactory<UserDto> aggregateFactory;

  private final ApplicationService<UserDto> applicationService;

  public UserApplicationServiceImpl(UserRepository repository, UserApplicationMessageBus messageBus,
                                    PersistenceRepository persistenceRepository) {
    this.repository = repository;
    this.messageBus = messageBus;
    this.persistenceRepository = persistenceRepository;
    this.aggregateFactory = new AggregateFactoryImpl<>(UserAggregate::of, UserAggregate::of);
    this.applicationService = ApplicationService.<UserDto>of(r -> repository.save(mapToDocument.apply(r)).map(mapToDto),
        this::publishChangedEvent, this::publishDocumentMessage);
  }

  private void publishChangedEvent(DomainEvent event) {
    Match.<UserChangedEvent>match(event)
        .when(UserCreated.class, e -> UserChangedEvent.builder()
            .aggregateId(e.aggregateId())
            .payload(mapCreatedToPayload.apply(e))
            .type(UserEventType.USER_CREATED)
            .build())
        .when(ContactInfoCreated.class, e -> UserChangedEvent.builder()
            .type(UserEventType.CONTACT_INFO_CREATED)
            .aggregateId(e.aggregateId())
            .payload(UserPayload.builder()
                .id(e.aggregateId())
                .version(e.userVersion())
                .createdAt(e.createdAt())
                .contactInfo(mapContactCreatedToPayload.apply(e))
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

  private void publishDocumentMessage(UserDto doc) {
    this.messageBus.publishDocumentMessage(doc);
  }

  @Override
  public Mono<UserDto> execute(Command cmd) {
    logger.debug(() -> "execute() ->");
    return Match.<Mono<UserDto>>match(cmd)
        .when(AddShortener.class, c ->
            persistenceRepository.execute(c.userId(), c, aggregateFactory)
                .flatMap(applicationService::processResult)
        )
        .when(CreateUser.class, this::createUser)
        .orElseGet(() -> Mono.empty());
  }

  @Override
  public Mono<UserDto> createUser(CreateUser command) {
    logger.debug(() -> "createUser() ->");
    return persistenceRepository.execute(UUID.randomUUID().toString(), command, aggregateFactory)
        .flatMap(applicationService::processResult);
  }

  @Override
  public Mono<UserDto> createContactInfo(String userId, CreateContactInfo command) {
    logger.debug(() -> "createContactInfo() ->");
    return persistenceRepository.execute(userId, command, aggregateFactory)
        .flatMap(applicationService::processResult);
  }
}
