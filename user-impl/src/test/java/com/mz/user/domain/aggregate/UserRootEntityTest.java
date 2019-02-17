package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.user.UserFunctions;
import com.mz.user.dto.ContactInfoDto;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.CreateContactInfo;
import com.mz.user.messages.CreateUser;
import com.mz.user.model.ContactInfoDocument;
import com.mz.user.model.UserDocument;
import org.eclipse.collections.impl.factory.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by zemi on 2019-01-16.
 */
class UserRootEntityTest {

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
        .contactInformation(CreateUser.ContactInfo
            .builder()
            .email("test@test")
            .phoneNumber("+421 901 000 000")
            .build())
        .build();
    createUserTest(cmd2);
  }

  private void createUserTest(Command cmd) {
    UserRootEntity subject = UserRootEntity.of();
    Optional<ApplyResult<UserDto>> result = subject.apply(cmd);
    Assertions.assertTrue(result.map(ApplyResult::event).isPresent());
  }

  @Test
  void ofTest() {
    UserDocument userDocument = new UserDocument(UUID.randomUUID().toString(), FIST_NAME, LAST_NAME, 1L,
        CREATED_AT, new ContactInfoDocument(EMAIL_EMAIL_COM, PHONE_NUMBER, CREATED_AT));
    UserDto userDto = UserFunctions.mapToDto.apply(userDocument);
    UserRootEntity subject = UserRootEntity.of(userDto);
    UserDto result = subject.toResult();
    ContactInfoDto contactInfoDto = result.contactInformation().get();

    ContactInfoDocument contactInfoDocument = userDocument.getContactInformationDocument();

    Assertions.assertTrue(result.equals(userDto));
    Assertions.assertTrue(userDto.id().get().equals(userDocument.getId()));
    Assertions.assertTrue(userDto.version().get().equals(userDocument.getVersion().get()));
    Assertions.assertTrue(userDto.firstName().get().equals(userDocument.getFirstName()));
    Assertions.assertTrue(userDto.lastName().get().equals(userDocument.getLastName()));
    Assertions.assertTrue(userDto.createdAt().equals(userDocument.getCreatedAt()));

    Assertions.assertTrue(contactInfoDto.createdAt().equals(contactInfoDocument.getCreatedAt()));
    Assertions.assertTrue(contactInfoDto.email().get().equals(contactInfoDocument.getEmail()));
    Assertions.assertTrue(contactInfoDto.phoneNumber().get().equals(contactInfoDocument.getPhoneNumber()));
    Assertions.assertTrue(contactInfoDto.userId().get().equals(userDocument.getId()));
  }

}