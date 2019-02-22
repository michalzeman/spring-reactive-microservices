package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.user.UserFunctions;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.ContactInfoPayload;
import com.mz.user.messages.commands.CreateContactInfo;
import com.mz.user.messages.commands.CreateUser;
import com.mz.user.model.ContactInfoDocument;
import com.mz.user.model.UserDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by zemi on 2019-01-16.
 */
class UserAggregateTest {

  static final String FIST_NAME = "FistName";
  static final String LAST_NAME = "LastName";
  static final String EMAIL_EMAIL_COM = "email@email.com";
  static final String PHONE_NUMBER = "+421 900 999 111";
  static final Instant CREATED_AT = Instant.now();

  @Test
  void createUserTest() {
    CreateUser cmd1 = CreateUser
        .builder()
        .firstName("FirstName")
        .lastName("LastName")
        .build();
    createUserTest(cmd1);

    CreateUser cmd2 = CreateUser
        .builder()
        .firstName("FirstName")
        .lastName("LastName")
        .contactInformation(ContactInfoPayload
            .builder()
            .email("test@test")
            .phoneNumber("+421 901 000 000")
            .build())
        .build();
    createUserTest(cmd2);
  }

  private void createUserTest(Command cmd) {
    UserAggregate subject = UserAggregate.of();
    Optional<ApplyResult<UserState>> result = subject.apply(cmd);
    Assertions.assertTrue(result.map(ApplyResult::event).isPresent());
  }

  @Test
  void ofTest() {
    UserDocument userDocument = new UserDocument(UUID.randomUUID().toString(), FIST_NAME, LAST_NAME, 1L,
        CREATED_AT, null);
    UserDto userDto = UserFunctions.mapToDto.apply(userDocument);
    UserAggregate subject = UserAggregate.of(userDto);
    subject.apply(CreateContactInfo.builder().email("test@test.com").build());
    UserState result = subject.toResult();
    UserState.ContactInfoState contactInfoState = result.contactInformation().get();


    Assertions.assertTrue(userDto.id().equals(userDocument.getId()));
    Assertions.assertTrue(userDto.version().equals(userDocument.getVersion()));
    Assertions.assertTrue(userDto.firstName().equals(userDocument.getFirstName()));
    Assertions.assertTrue(userDto.lastName().equals(userDocument.getLastName()));
    Assertions.assertTrue(userDto.createdAt().equals(userDocument.getCreatedAt()));

//    Assertions.assertTrue(contactInfoState.createdAt().equals(contactInfoDocument.getCreatedAt()));
    Assertions.assertTrue(contactInfoState.email().get().equals("test@test.com"));
  }

}