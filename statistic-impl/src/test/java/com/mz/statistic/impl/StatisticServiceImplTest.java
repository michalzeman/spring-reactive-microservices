package com.mz.statistic.impl;

import com.mz.reactivedemo.shortener.api.events.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.events.ShortenerViewed;
import com.mz.statistic.ShortenerSubscriber;
import com.mz.statistic.StatisticRepository;
import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.function.Consumer;

import static com.mz.reactivedemo.shortener.api.topics.ShortenerTopics.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by zemi on 29/05/2018.
 */
@ExtendWith(SpringExtension.class)
//@DataMongoTest
public class StatisticServiceImplTest {

  @Mock
  StatisticRepository repository;

  FluxSink<ShortenerViewed> eventSink;

  ShortenerSubscriber shortenerSubscriber = new ShortenerSubscriberMockImpl((sink) -> eventSink = sink);

  @InjectMocks
  StatisticServiceImpl stub = new StatisticServiceImpl(repository, shortenerSubscriber);

//  @ClassRule
//  public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, SHORTENER_CHANGED,
//      SHORTENER_DOCUMENT, SHORTENER_VIEWED);
//
//  @BeforeClass
//  public static void setup() {
//    System.setProperty("spring.cloud.stream.kafka.binder.brokers", embeddedKafka.getBrokersAsString());
//  }

  //  @BeforeAll
  public void beforeAll() {
    stub.subscribeToEvents();
  }

  @Test
  public void getAll() {
    StatisticDocument statisticDocument1 = new StatisticDocument();
    statisticDocument1.setNumber(1L);
    statisticDocument1.setUrl("qwwergr");
    statisticDocument1.setId("Stat1");

    Mockito.when(repository.findAll()).thenReturn(Flux.just(statisticDocument1));

    Flux<StatisticDocument> source = stub.getAll();
    StepVerifier.create(source)
        .expectNext(statisticDocument1)
        .expectComplete().verify();
  }

  @Test
  public void numberOfViews() {
    StatisticDocument statisticDocument1 = new StatisticDocument();
    statisticDocument1.setNumber(1L);
    statisticDocument1.setUrl("qwwergr");
    statisticDocument1.setId("Stat1");

    StatisticDocument statisticDocument2 = new StatisticDocument();
    statisticDocument2.setNumber(1L);
    statisticDocument2.setUrl("qwwergr");
    statisticDocument2.setId("Stat2");

    Mockito.when(repository.findByUrlAndEventType(any(String.class), any(EventType.class))).thenReturn(Flux.just
        (statisticDocument1,
            statisticDocument2));

    Mono<Long> source = stub.numberOfViews("qwwergr");
    StepVerifier.create(source)
        .expectNext(2L)
        .expectComplete().verify();
  }

  @Test
  public void processEvent() {
    String keyUrl1 = "keyUrl1";
    String eventId = "EventId";
    ShortenerViewed event1 = ShortenerViewed.builder()
        .number(1L)
        .createdAt(Instant.now())
        .key(keyUrl1)
        .id(eventId)
        .build();
    ShortenerViewed event2 = ShortenerViewed.builder()
        .number(1L)
        .createdAt(Instant.now())
        .key(keyUrl1)
        .id(eventId)
        .build();

    StatisticDocument statisticDocument = new StatisticDocument();
    statisticDocument.setNumber(event1.number());
    statisticDocument.setUrl(event1.key());
    statisticDocument.setCreatedAt(event1.createdAt());
    statisticDocument.setEventId(event1.id());

    eventSink.next(event1);
    eventSink.next(event2);

    Mockito.when(repository.findByEventId(eventId)).thenReturn(Flux.just(statisticDocument));
    Mockito.verify(repository, Mockito.atMost(1)).save(any(StatisticDocument.class));
  }

  static class ShortenerSubscriberMockImpl implements ShortenerSubscriber {

    private final ReplayProcessor<ShortenerViewed> events = ReplayProcessor.create(1);

    private final FluxSink<ShortenerViewed> eventSink = events.sink();

    public ShortenerSubscriberMockImpl(Consumer<FluxSink<ShortenerViewed>> eventEmitter) {
      eventEmitter.accept(eventSink);
    }

    @Override
    public Flux<ShortenerViewed> eventsShortenerViewed() {
      return events.publishOn(Schedulers.parallel());
    }

    @Override
    public Flux<ShortenerChangedEvent> shortenerChanged() {
      return Flux.empty();
    }

  }
}