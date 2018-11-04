package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.common.model.ErrorMessage;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.ImmutableCreateShortener;
import com.mz.reactivedemo.shortener.api.commands.ImmutableUpdateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by zemi on 29/05/2018.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShortenerHandlerTest {

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


  @Test
  public void tick() {
    webTestClient.get().uri("/shorteners/health/ticks").accept(MediaType.APPLICATION_JSON_UTF8).exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody();
  }

  @Test
  public void get() {

    String key = service.create(CreateShortener.builder()
        .url("www.tes.com")
        .build())
        .block().key();

    webTestClient.get().uri("/shorteners/map/{key}", key).accept(MediaType.APPLICATION_JSON_UTF8).exchange()
        .expectStatus()
        .is3xxRedirection()
        .expectBody();
  }

  @Test
  public void getAll() {

    service.create(CreateShortener.builder()
        .url("www.tes.com")
        .build())
        .block().key();

    webTestClient.get().uri("/shorteners").accept(MediaType.APPLICATION_JSON_UTF8).exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody();
  }

  @Test
  public void create() {
    String url = "www.testLong.org";
    CreateShortener request = CreateShortener.builder()
        .url(url)
        .build();

    ShortenerDTO result = webTestClient.post().uri("/shorteners")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody(ShortenerDTO.class).returnResult().getResponseBody();

    assertTrue(result.url().equals(url));
    assertFalse(result.key().isEmpty());
    assertTrue(result.id().isPresent());
  }

  @Test
  public void update() {

    String id = service.create(CreateShortener.builder()
        .url("www.tes.com").build())
        .block().id().get();

    String url = "www.testLongUpdate.org";
    UpdateShortener request = UpdateShortener.builder()
        .id(id)
        .url(url).build();

    ShortenerDTO result = webTestClient.put().uri("/shorteners/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(request))
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody(ShortenerDTO.class).returnResult().getResponseBody();

    ShortenerDTO validateResult = webTestClient.get().uri("/shorteners/{id}", result.id().get()).accept(MediaType
        .APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody(ShortenerDTO.class).returnResult().getResponseBody();

    assertTrue(validateResult.url().equals(url));
  }

  @Test
  public void getError() {
    ErrorMessage errorResult = webTestClient.get().uri("/shorteners/errors")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus()
        .is4xxClientError()
        .expectBody(ErrorMessage.class).returnResult().getResponseBody();
    System.out.println("Error Zemo: -> "+errorResult.getError());
  }
}