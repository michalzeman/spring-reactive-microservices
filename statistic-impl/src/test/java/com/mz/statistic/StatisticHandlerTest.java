package com.mz.statistic;

import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by zemi on 22/10/2018.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticHandlerTest {

  @Autowired
  WebTestClient webTestClient;

  @Autowired
  StatisticRepository repository;

  @AfterEach
  void afterEach() {
    repository.deleteAll().block();
  }

  @Test
  public void getAllTest() {
    StatisticDocument statisticDocument1 = new StatisticDocument(UUID.randomUUID().toString(),
        UUID.randomUUID().toString(), Instant.now(), EventType.SHORTENER_VIEWED, UUID.randomUUID().toString());
    repository.save(statisticDocument1).subscribe();
    StatisticDocument statisticDocument2 = new StatisticDocument(UUID.randomUUID().toString(), UUID
        .randomUUID().toString(), Instant.now(), EventType.SHORTENER_VIEWED, UUID.randomUUID().toString());
    repository.save(statisticDocument2).subscribe();

    webTestClient.get().uri("/statistics").accept(MediaType
        .APPLICATION_JSON).exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  @Test
  public void countsTest() {
    EventType eventType = EventType.SHORTENER_UPDATED;
    StatisticDocument statisticDocument1 = new StatisticDocument(UUID.randomUUID().toString(), UUID
        .randomUUID().toString(), Instant.now(), EventType.SHORTENER_UPDATED, UUID.randomUUID().toString());

    Long resultBefore =
        webTestClient.get().uri("/statistics/shorteners/events/{eventType}/counts", eventType).accept(MediaType
            .APPLICATION_JSON).exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Long.class).returnResult().getResponseBody();
    Assertions.assertNotNull(resultBefore);

    repository.save(statisticDocument1).subscribe();
    StatisticDocument statisticDocument2 = new StatisticDocument(UUID.randomUUID().toString(), UUID
        .randomUUID().toString(), Instant.now(), EventType.SHORTENER_UPDATED, UUID.randomUUID().toString());
    repository.save(statisticDocument2).subscribe();

    StatisticDocument statisticDocument3 = new StatisticDocument(UUID.randomUUID().toString(), UUID
        .randomUUID().toString(), Instant.now(), EventType.SHORTENER_CREATED, UUID.randomUUID().toString());
    repository.save(statisticDocument3).subscribe();

    Long resultAfter =
        webTestClient.get().uri("/statistics/shorteners/events/{eventType}/counts", eventType).accept(MediaType
            .APPLICATION_JSON).exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(Long.class).returnResult().getResponseBody();
    Assertions.assertNotNull(resultAfter);
    Assertions.assertNotEquals(resultBefore, resultAfter);
  }


}
