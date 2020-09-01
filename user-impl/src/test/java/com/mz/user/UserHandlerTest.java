package com.mz.user;

import com.mz.user.dto.UserDto;
import com.mz.user.message.command.CreateContactInfo;
import com.mz.user.message.command.CreateUser;
import com.mz.user.view.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

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
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    assertEquals(result.firstName(), firstNameTest);
    assertEquals(result.lastName(), lastNameTest);
    assertFalse(result.id().isEmpty());

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
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    assertTrue(result.firstName().equals(firstNameTest));
    assertTrue(result.lastName().equals(lastNameTest));
    assertTrue(!result.id().isEmpty());

    CreateContactInfo createContactInfo = CreateContactInfo.builder().email("test@test.com").build();

    UserDto result2 = webTestClient.put().uri("/users/{userId}/contactinformation", result.id())
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(createContactInfo))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    assertFalse(result2.version().equals(result.version()));
    assertTrue(result2.contactInformation().isPresent());
    assertTrue(!result.id().isEmpty());
  }

  @Test
  void getById() {
    String firstNameTest = "FirstNameTest";
    String lastNameTest = "LastNameTest";
    CreateUser request = CreateUser.builder()
        .firstName(firstNameTest)
        .lastName(lastNameTest)
        .build();

    UserDto result = webTestClient.post().uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    CreateContactInfo createContactInfo = CreateContactInfo.builder().email("test@test.com").build();

    webTestClient.put().uri("/users/{userId}/contactinformation", result.id())
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(createContactInfo))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    UserDto resultGet = webTestClient.get().uri("/users/{id}", result.id())
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    assertEquals(firstNameTest, resultGet.firstName());
    assertEquals(lastNameTest, resultGet.lastName());
    assertTrue(resultGet.contactInformation().isPresent());
    assertEquals(resultGet.contactInformation().get().email().get(), "test@test.com");
  }

  @Test
  void getAll() {
    List<UserDto> resultGetBefore = webTestClient.get().uri("/users")
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(List.class).returnResult().getResponseBody();

    String firstNameTest = "FirstNameTest";
    String lastNameTest = "LastNameTest";
    CreateUser request = CreateUser.builder()
        .firstName(firstNameTest)
        .lastName(lastNameTest)
        .build();

    webTestClient.post().uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    webTestClient.post().uri("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDto.class).returnResult().getResponseBody();

    List<UserDto> resultGetAfter = webTestClient.get().uri("/users")
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(List.class).returnResult().getResponseBody();

    assertEquals(resultGetAfter.size() - resultGetBefore.size(), 2);
  }
}
