package com.mz.user.impl;

import com.mz.reactivedemo.adapter.persistance.persistence.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.persistence.PersistenceRepository;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.UserFunctions;
import com.mz.user.domain.event.ContactInfoCreated;
import com.mz.user.domain.event.UserCreated;
import com.mz.user.dto.UserDto;
import com.mz.user.message.command.CreateContactInfo;
import com.mz.user.message.command.CreateUser;
import com.mz.user.message.event.UserChangedEvent;
import com.mz.user.message.event.UserEventType;
import com.mz.user.view.ContactInfoDocument;
import com.mz.user.view.UserDocument;
import com.mz.user.view.UserRepository;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceImplTest {

  @Mock
  UserRepository userRepository;

  @Mock
  UserApplicationMessageBus messageBus;

  @Mock
  PersistenceRepository persistenceRepository;

  @InjectMocks
  UserApplicationServiceImpl stub;

  @Test
  void createUserTest() {

    String id = UUID.randomUUID().toString();

    String lastName = "TestLastName";

    String firstName = "TestFirstName";

    Instant createdAt = Instant.now();

    CreateUser cmd = CreateUser.builder()
        .lastName(lastName)
        .firstName(firstName)
        .build();

    UserDocument userDocument = new UserDocument(id, firstName, lastName, 1L, createdAt, null);

    ArgumentCaptor<UserChangedEvent> argument = ArgumentCaptor.forClass(UserChangedEvent.class);

    when(userRepository.save(any(UserDocument.class))).thenReturn(Mono.just(userDocument));

    when(persistenceRepository.create(any(String.class), any(CreateUser.class), any(AggregateFactory.class)))
        .thenReturn(Mono.just(CommandResult.of(UserFunctions.mapToDto.apply(userDocument),
            Lists.immutable.of(UserCreated.builder()
                .firstName(firstName)
                .lastName(lastName)
                .id(id)
                .version(1L)
                .build()))));

    Mono<UserDto> result = stub.createUser(cmd);

    StepVerifier.create(result)
        .expectNextMatches(nextValue ->
            nextValue.lastName().equals(lastName)
                && nextValue.firstName().equals(firstName)
                && nextValue.id().equals(id)
                && nextValue.createdAt().equals(createdAt)
                && nextValue.version().equals(1L))
        .expectComplete().verify();

    Mockito.verify(messageBus).publishEvent(argument.capture());
    Assertions.assertTrue(argument.getValue().type().equals(UserEventType.USER_CREATED));
    Assertions.assertTrue(argument.getValue().payload().lastName().get().equals(lastName));
    Assertions.assertTrue(argument.getValue().payload().firstName().get().equals(firstName));
    Assertions.assertTrue(argument.getValue().payload().id().equals(id));
    Assertions.assertTrue(argument.getValue().payload().version().equals(1L));
  }

  @Test
  void createContactInfo() {
    String id = UUID.randomUUID().toString();

    String lastName = "TestLastName";

    String firstName = "TestFirstName";

    Instant createdAt = Instant.now();

    UserDocument userDocument = new UserDocument(id, firstName, lastName, 1L, createdAt, null);

    String email = "test@test.com";
    String phoneNumber = "+420900000000";
    Instant nowContactInfo = Instant.now();

    ContactInfoDocument contactInfoDocument = new ContactInfoDocument(email, phoneNumber, nowContactInfo);
    UserDocument userDocumentUpdated = new UserDocument(id, firstName, lastName, 2L, createdAt, contactInfoDocument);

    ArgumentCaptor<UserChangedEvent> argument = ArgumentCaptor.forClass(UserChangedEvent.class);

    when(userRepository.save(any(UserDocument.class))).thenReturn(Mono.just(userDocumentUpdated));

    when(persistenceRepository.update(any(String.class), any(CreateContactInfo.class)))
        .thenReturn(Mono.just(CommandResult.of(UserFunctions.mapToDto.apply(userDocument),
            Lists.immutable.of(ContactInfoCreated.builder()
                .email(email)
                .phoneNumber(phoneNumber)
                .userId(id)
                .userVersion(2L)
                .createdAt(createdAt)
                .build()))));

    Mono<UserDto> result = stub.createContactInfo(id,
        CreateContactInfo.builder().email(email).phoneNumber(phoneNumber).build());

    StepVerifier.create(result)
        .expectNextMatches(nextValue ->
            nextValue.lastName().equals(lastName)
                && nextValue.firstName().equals(firstName)
                && nextValue.id().equals(id)
                && nextValue.createdAt().equals(createdAt)
                && nextValue.version().equals(2L)
                && nextValue.contactInformation().isPresent()
                && nextValue.contactInformation().get().userId().equals(id)
                && nextValue.contactInformation().get().phoneNumber().get().equals(phoneNumber)
                && nextValue.contactInformation().get().email().get().equals(email)
                && nextValue.contactInformation().get().createdAt().equals(nowContactInfo)
        )
        .expectComplete().verify();

    Mockito.verify(messageBus).publishEvent(argument.capture());
    Assertions.assertTrue(argument.getValue().type().equals(UserEventType.CONTACT_INFO_CREATED));
    Assertions.assertTrue(argument.getValue().payload().contactInfo().get().email().get().equals(email));
    Assertions.assertTrue(argument.getValue().payload().contactInfo().get().phoneNumber().get().equals(phoneNumber));
    Assertions.assertTrue(argument.getValue().payload().id().equals(id));
    Assertions.assertTrue(argument.getValue().payload().version().equals(2L));
    Assertions.assertTrue(argument.getValue().payload().createdAt().equals(createdAt));
  }
}
