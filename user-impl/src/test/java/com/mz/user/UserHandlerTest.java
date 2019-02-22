package com.mz.user;

import com.mz.user.dto.UserDto;
import com.mz.user.messages.commands.CreateContactInfo;
import com.mz.user.messages.commands.CreateUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserHandlerTest {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  UserRepository repository;


  @AfterEach
  void afterEach() {
    repository.deleteAll().block();
  }

  @Test
  void createUser() {
    String firstNameTest = "FirstNameTest";
    String lastNameTest = "LastNameTest";
    CreateUser request = CreateUser.builder()
        .firstName(firstNameTest)
        .lastName(lastNameTest)
        .build();

    UserDto result = webTestClient.post().uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    assertTrue(result.firstName().equals(firstNameTest));
    assertTrue(result.lastName().equals(lastNameTest));
    assertTrue(!result.id().isEmpty());

  }

  @Test
  void createContactInfo() {
    String firstNameTest = "FirstNameTest";
    String lastNameTest = "LastNameTest";
    CreateUser request = CreateUser.builder()
        .firstName(firstNameTest)
        .lastName(lastNameTest)
        .build();

    UserDto result = webTestClient.post().uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    assertTrue(result.firstName().equals(firstNameTest));
    assertTrue(result.lastName().equals(lastNameTest));
    assertTrue(!result.id().isEmpty());

    CreateContactInfo createContactInfo = CreateContactInfo.builder().email("test@test.com").build();

    UserDto result2 = webTestClient.put().uri("/users/{userId}", result.id())
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(createContactInfo))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    assertFalse(result2.version().equals(result.version()));
    assertTrue(result2.contactInformation().isPresent());
    assertTrue(!result.id().isEmpty());
  }
}