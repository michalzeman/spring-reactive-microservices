package com.mz.user.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.user.UserMapper;
import com.mz.user.dto.UserDto;
import com.mz.user.message.command.CreateContactInfo;
import com.mz.user.message.command.CreateUser;
import com.mz.user.view.ContactInfoDocument;
import com.mz.user.view.UserDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAggregateServiceImplTest {

//  @Mock
//  UserRepository userRepository;
//
//  @Mock
//  UserApplicationMessageBus messageBus;
//
//  @Mock
//  AggregateRepository aggregateRepository;

  @Mock
  AggregateService<UserDto> aggregateService;

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

//    UserDocument userDocument = new UserDocument(id, firstName, lastName, 1L, createdAt, null);

    var userDto = UserDto.builder()
        .id(id)
        .version(1L)
        .createdAt(createdAt)
        .firstName(firstName)
        .lastName(lastName)
        .build();

//    ArgumentCaptor<UserChangedEvent> argument = ArgumentCaptor.forClass(UserChangedEvent.class);

    when(aggregateService.execute(any(String.class), any(Command.class))).thenReturn(Mono.just(userDto));
//    when(userRepository.save(any(UserDocument.class))).thenReturn(Mono.just(userDocument));
//
//    when(aggregateRepository.execute(any(String.class), any(CreateUser.class), any(AggregateFactory.class)))
//        .thenReturn(Mono.just(CommandResult.of(UserMapper.mapToDto.apply(userDocument),
//            Lists.immutable.of(UserCreated.builder()
//                .firstName(firstName)
//                .lastName(lastName)
//                .aggregateId(id)
//                .version(1L)
//                .build()))));

    Mono<UserDto> result = stub.createUser(cmd);

    StepVerifier.create(result)
        .expectNextMatches(nextValue ->
            nextValue.lastName().equals(lastName)
                && nextValue.firstName().equals(firstName)
                && nextValue.id().equals(id)
                && nextValue.createdAt().equals(createdAt)
                && nextValue.version().equals(1L))
        .expectComplete().verify();

//    Mockito.verify(messageBus).publishEvent(argument.capture());
//    Assertions.assertTrue(argument.getValue().type().equals(UserEventType.USER_CREATED));
//    Assertions.assertTrue(argument.getValue().payload().lastName().get().equals(lastName));
//    Assertions.assertTrue(argument.getValue().payload().firstName().get().equals(firstName));
//    Assertions.assertTrue(argument.getValue().payload().id().equals(id));
//    Assertions.assertTrue(argument.getValue().payload().version().equals(1L));
  }

  @Test
  void createContactInfo() {
    String id = UUID.randomUUID().toString();

    String lastName = "TestLastName";

    String firstName = "TestFirstName";

    Instant createdAt = Instant.now();


    String email = "test@test.com";
    String phoneNumber = "+420900000000";
    Instant nowContactInfo = Instant.now();

    ContactInfoDocument contactInfoDocument = new ContactInfoDocument(email, phoneNumber, nowContactInfo);
    UserDocument userDocumentUpdated = new UserDocument(id, firstName, lastName, 2L, createdAt, contactInfoDocument);

    var userDto = UserMapper.FN.mapToDto.apply(userDocumentUpdated);
    when(aggregateService.execute(any(String.class), any(Command.class))).thenReturn(Mono.just(userDto));

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

//    Mockito.verify(messageBus).publishEvent(argument.capture());
//    Assertions.assertTrue(argument.getValue().type().equals(UserEventType.CONTACT_INFO_CREATED));
//    Assertions.assertTrue(argument.getValue().payload().contactInfo().get().email().get().equals(email));
//    Assertions.assertTrue(argument.getValue().payload().contactInfo().get().phoneNumber().get().equals(phoneNumber));
//    Assertions.assertTrue(argument.getValue().payload().id().equals(id));
//    Assertions.assertTrue(argument.getValue().payload().version().equals(2L));
//    Assertions.assertTrue(argument.getValue().payload().createdAt().equals(createdAt));
  }
}
