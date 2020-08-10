package com.mz.statistic.impl;

import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerViewed;
import com.mz.statistic.StatisticRepository;
import com.mz.statistic.adapters.shortener.ShortenerSubscriber;
import com.mz.statistic.adapters.user.UserSubscriber;
import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
import com.mz.user.message.event.UserChangedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by zemi on 29/05/2018.
 */
@ExtendWith(SpringExtension.class)
public class StatisticServiceImplTest {

  StatisticRepository repository = Mockito.mock(StatisticRepository.class);

  FluxSink<ShortenerViewed> eventSink;

  FluxSink<UserChangedEvent> userChangedEventSink;

  ShortenerSubscriber shortenerSubscriber = new ShortenerSubscriberMockImpl((sink) -> eventSink = sink);

  UserSubscriber userSubscriber = new UserSubscriberMockImpl(sink -> userChangedEventSink = sink);

  StatisticServiceImpl stub = new StatisticServiceImpl(repository, shortenerSubscriber, userSubscriber);


  @Test
  public void getAll() {
    StatisticDocument statisticDocument1 = new StatisticDocument();
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
    statisticDocument1.setId("Stat1");
    statisticDocument1.setEventType(EventType.SHORTENER_VIEWED);

    StatisticDocument statisticDocument2 = new StatisticDocument();
    statisticDocument2.setId("Stat2");
    statisticDocument2.setEventType(EventType.SHORTENER_VIEWED);

    Mockito.when(repository.findByEventType(any(EventType.class))).thenReturn(Flux.just
        (statisticDocument1,
            statisticDocument2));

    Mono<Long> source = stub.eventsCount(EventType.SHORTENER_VIEWED);
    StepVerifier.create(source)
        .expectNext(2L)
        .expectComplete().verify();
  }

  @Test
  public void processEvent() {
    String keyUrl1 = "keyUrl1";
    String eventId = "EventId";
    ShortenerViewed event1 = ShortenerViewed.builder()
        .aggregateId(UUID.randomUUID().toString())
        .number(1L)
        .key(keyUrl1)
        .eventId(eventId)
        .build();
    ShortenerViewed event2 = ShortenerViewed.builder()
        .aggregateId(UUID.randomUUID().toString())
        .number(1L)
        .key(keyUrl1)
        .eventId(eventId)
        .build();

    StatisticDocument statisticDocument = new StatisticDocument();
    statisticDocument.setCreatedAt(event1.eventCreatedAt());
    statisticDocument.setEventId(event1.eventId());

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

  static class UserSubscriberMockImpl implements UserSubscriber {

    private final ReplayProcessor<UserChangedEvent> events = ReplayProcessor.create(1);

    private final FluxSink<UserChangedEvent> eventSink = events.sink();

    public UserSubscriberMockImpl(Consumer<FluxSink<UserChangedEvent>> eventEmitter) {
      eventEmitter.accept(eventSink);
    }

    @Override
    public Flux<UserChangedEvent> userChanged() {
      return events.publishOn(Schedulers.parallel());
    }
  }
}
