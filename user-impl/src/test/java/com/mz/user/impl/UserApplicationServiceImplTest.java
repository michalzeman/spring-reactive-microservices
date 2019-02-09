package com.mz.user.impl;

import com.mz.user.UserRepository;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.CreateUser;
import com.mz.user.model.UserDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceImplTest {

  @Mock
  UserRepository userRepository;

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

    when(userRepository.save(any(UserDocument.class))).thenReturn(Mono.just(userDocument));

    Mono<UserDto> result = stub.createUser(cmd);

    StepVerifier.create(result)
        .expectNextMatches(nextValue ->
            nextValue.lastName().get().equals(lastName)
                && nextValue.firstName().get().equals(firstName)
                && nextValue.id().get().equals(id)
                && nextValue.createdAt().equals(createdAt)
                && nextValue.version().get().equals(1L))
        .expectComplete().verify();
  }
}