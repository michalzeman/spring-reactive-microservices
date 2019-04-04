package com.mz.statistic;

import com.mz.reactivedemo.shortener.api.event.ShortenerEventType;
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
  StatisticService service;

  @Autowired
  StatisticRepository repository;

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
//    System.setProperty("spring.cloud.stream.kafka.binder.zkNodes", embeddedKafka.getZookeeperConnectionString());
//  }

  @Test
  public void getAllTest() {
    String urlKey = UUID.randomUUID().toString();
    StatisticDocument statisticDocument1 = new StatisticDocument(UUID.randomUUID().toString(), urlKey, 1L, UUID.randomUUID().toString(), Instant.now(), EventType.VIEWED);
    repository.save(statisticDocument1).subscribe();
    StatisticDocument statisticDocument2 = new StatisticDocument(UUID.randomUUID().toString(), urlKey, 1L, UUID
        .randomUUID().toString(), Instant.now(), EventType.VIEWED);
    repository.save(statisticDocument2).subscribe();

    webTestClient.get().uri("/statistics").accept(MediaType
        .APPLICATION_JSON_UTF8).exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBody();
  }

  @Test
  public void numberOfViewTest() {
    String urlKey = UUID.randomUUID().toString();
    StatisticDocument statisticDocument1 = new StatisticDocument(UUID.randomUUID().toString(), urlKey, 1L, UUID.randomUUID().toString(), Instant.now(), EventType.VIEWED);
    repository.save(statisticDocument1).subscribe();
    StatisticDocument statisticDocument2 = new StatisticDocument(UUID.randomUUID().toString(), urlKey, 1L, UUID
        .randomUUID().toString(), Instant.now(), EventType.VIEWED);
    repository.save(statisticDocument2).subscribe();

    Long result =
        webTestClient.get().uri("/statistics/shorteners/{key}", urlKey).accept(MediaType
            .APPLICATION_JSON_UTF8).exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody(Long.class).returnResult().getResponseBody();
    Assertions.assertEquals(result.longValue(), 2L);
  }

  @Test
  public void countsTest() {
    ShortenerEventType eventType = ShortenerEventType.UPDATED;
    StatisticDocument statisticDocument1 = new StatisticDocument(UUID.randomUUID().toString(), "", 1L, UUID
        .randomUUID().toString(), Instant.now(), EventType.UPDATED);
    repository.save(statisticDocument1).subscribe();
    StatisticDocument statisticDocument2 = new StatisticDocument(UUID.randomUUID().toString(), "", 1L, UUID
        .randomUUID().toString(), Instant.now(), EventType.UPDATED);
    repository.save(statisticDocument2).subscribe();

    StatisticDocument statisticDocument3 = new StatisticDocument(UUID.randomUUID().toString(), "", 1L, UUID
        .randomUUID().toString(), Instant.now(), EventType.CREATED);
    repository.save(statisticDocument3).subscribe();

    Long result =
        webTestClient.get().uri("/statistics/shorteners/events/{eventType}/counts", eventType).accept(MediaType
            .APPLICATION_JSON_UTF8).exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBody(Long.class).returnResult().getResponseBody();
    Assertions.assertEquals(result.longValue(), 2L);
  }


}
