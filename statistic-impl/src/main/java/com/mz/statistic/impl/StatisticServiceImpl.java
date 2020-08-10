package com.mz.statistic.impl;

import com.mz.reactivedemo.common.util.Logger;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerViewed;
import com.mz.statistic.StatisticRepository;
import com.mz.statistic.StatisticService;
import com.mz.statistic.adapters.shortener.ShortenerSubscriber;
import com.mz.statistic.adapters.user.UserSubscriber;
import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
import com.mz.user.message.event.UserChangedEvent;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by zemi on 29/05/2018.
 */
@Service
public class StatisticServiceImpl implements StatisticService {

  private static final Logger log = new Logger(LogFactory.getLog(StatisticServiceImpl.class));

  private final StatisticRepository repository;

  private final ShortenerSubscriber shortenerSubscriber;

  private final UserSubscriber userSubscriber;


  @Autowired
  public StatisticServiceImpl(StatisticRepository repository,
                              ShortenerSubscriber shortenerSubscriber,
                              UserSubscriber userSubscriber) {
    this.repository = repository;
    this.shortenerSubscriber = shortenerSubscriber;
    this.userSubscriber = userSubscriber;
    subscribeToEvents();
  }

  public void subscribeToEvents() {
    shortenerSubscriber.eventsShortenerViewed()
        .flatMap(this::processViewedEvent)
        .doOnError(exp -> log.log().error("eventsShortenerViewed event stream error", exp))
        .retry()
        .subscribe();
    shortenerSubscriber.shortenerChanged()
        .flatMap(this::processShortenerChangedEvent)
        .doOnError(exp -> log.log().error("shortenerChanged event stream error", exp))
        .retry()
        .subscribe();
    userSubscriber.userChanged()
        .flatMap(this::processUserChangedEvent)
        .doOnError(exp -> log.log().error("userChanged event stream error", exp))
        .retry()
        .subscribe();
  }

  private Mono<StatisticDocument> processViewedEvent(ShortenerViewed event) {
    return repository.findByEventId(event.eventId())
        .next()
        .switchIfEmpty(Mono.defer(() -> {
          StatisticDocument statisticDocument = new StatisticDocument();
          statisticDocument.setCreatedAt(event.eventCreatedAt());
          statisticDocument.setEventId(event.eventId());
          statisticDocument.setEventType(EventType.SHORTENER_VIEWED);
          statisticDocument.setAggregateId(event.aggregateId());
          return  repository.save(statisticDocument);
        }));
  }

  private Mono<StatisticDocument> processShortenerChangedEvent(ShortenerChangedEvent event) {
    StatisticDocument statisticDocument = new StatisticDocument();
    statisticDocument.setCreatedAt(event.eventCreatedAt());
    statisticDocument.setEventId(event.eventId());
    statisticDocument.setAggregateId(event.aggregateId());
    switch (event.type()) {
      case CREATED:
        statisticDocument.setEventType(EventType.SHORTENER_CREATED);
        break;
      case UPDATED:
        statisticDocument.setEventType(EventType.SHORTENER_UPDATED);
        break;
      case DELETED:
        statisticDocument.setEventType(EventType.SHORTENER_DELETED);
        break;
    }
    return repository.save(statisticDocument);
  }

  private Mono<StatisticDocument> processUserChangedEvent(UserChangedEvent event) {
    StatisticDocument statisticDocument = new StatisticDocument();
    statisticDocument.setCreatedAt(event.eventCreatedAt());
    statisticDocument.setEventId(event.eventId());
    statisticDocument.setAggregateId(event.aggregateId());
    switch (event.type()) {
      case USER_CREATED:
        statisticDocument.setEventType(EventType.USER_CREATED);
        break;
      case USER_UPDATED:
      case CONTACT_INFO_CREATED:
        statisticDocument.setEventType(EventType.USER_UPDATED);
        break;
    }
    return repository.save(statisticDocument);
  }

  @Override
  public Flux<StatisticDocument> getAll() {
    log.debug(() -> "getAll() ->");
    return repository.findAll()
        .takeLast(100);
  }

  @Override
  public Mono<Long> eventsCount(EventType type) {
    return repository.findByEventType(type)
        .map(r -> 1)
        .reduce(0L, Long::sum);
  }

}
