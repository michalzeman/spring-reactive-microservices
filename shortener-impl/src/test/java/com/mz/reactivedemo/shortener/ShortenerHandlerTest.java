package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.common.http.ErrorMessage;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by zemi on 29/05/2018.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShortenerHandlerTest {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  ShortenerService service;

  @Autowired
  ShortenerRepository repository;

  @AfterEach
  void afterEach() {
    repository.deleteAll().block();
  }

//  @ClassRule
//  public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, SHORTENER_CHANGED,
//      SHORTENER_DOCUMENT, SHORTENER_VIEWED);
//
//  @BeforeClass
//  public static void setup() {
//    System.setProperty("spring.cloud.stream.kafka.binder.brokers", embeddedKafka.getBrokersAsString());
//  }


  @Test
  public void tick() {
    webTestClient.get().uri("/shorteners/health/ticks").accept(MediaType.APPLICATION_JSON).exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  @Test
  public void get() {
    String userId = UUID.randomUUID().toString();
    String key = service.create(CreateShortener.builder()
        .url("www.tes.com")
        .userId(userId)
        .build())
        .block().key();

    webTestClient.get().uri("/shorteners/map/{key}", key).accept(MediaType.APPLICATION_JSON).exchange()
        .expectStatus()
        .is3xxRedirection()
        .expectBody();
  }

  @Test
  public void getAll() {
    String userId = UUID.randomUUID().toString();
    service.create(CreateShortener.builder()
        .url("www.tes.com")
        .userId(userId)
        .build())
        .block().key();

    webTestClient.get().uri("/shorteners").accept(MediaType.APPLICATION_JSON).exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  @Test
  public void create() {
    String url = "www.testLong.org";

    String userId = UUID.randomUUID().toString();
    CreateShortener request = CreateShortener.builder()
        .url(url)
        .userId(userId)
        .build();

    ShortenerDto result = webTestClient.post().uri("/shorteners")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(ShortenerDto.class).returnResult().getResponseBody();

    assertTrue(result.url().equals(url));
    assertFalse(result.key().isEmpty());
    assertTrue(!result.id().isEmpty());
  }

  @Test
  public void update() {
    String userId = UUID.randomUUID().toString();
    CreateShortener createRequest = CreateShortener.builder()
        .url("www.testLong.org")
        .userId(userId)
        .build();

    String id = webTestClient.post().uri("/shorteners")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(createRequest))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(ShortenerDto.class).returnResult().getResponseBody().id();

    String url = "www.testLongUpdate.org";
    UpdateShortener request = UpdateShortener.builder()
        .id(id)
        .url(url).build();

    webTestClient.put().uri("/shorteners/{eventId}", id).accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectStatus().is2xxSuccessful();

    ShortenerDto validateResult = webTestClient.get().uri("/shorteners/{eventId}", id).accept(MediaType
        .APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(ShortenerDto.class).returnResult().getResponseBody();

    assertTrue(validateResult.url().equals(url));
  }

  @Test
  public void getError() {
    ErrorMessage errorResult = webTestClient.get().uri("/shorteners/errors")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody(ErrorMessage.class).returnResult().getResponseBody();
    System.out.println("Error Zemo: -> " + errorResult.error());
  }
}
