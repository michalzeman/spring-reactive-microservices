package com.mz.statistic.impl;

import com.mz.reactivedemo.common.utils.Logger;
import com.mz.reactivedemo.shortener.api.events.ShortenerChangedEvent;
import com.mz.statistic.ShortenerSubscriber;
import com.mz.reactivedemo.shortener.api.events.ShortenerViewed;
import com.mz.statistic.StatisticRepository;
import com.mz.statistic.StatisticService;
import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
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

  private StatisticRepository repository;

  private ShortenerSubscriber shortenerSubscriber;


  @Autowired
  public StatisticServiceImpl(StatisticRepository repository,
                              ShortenerSubscriber shortenerSubscriber) {
    this.repository = repository;
    this.shortenerSubscriber = shortenerSubscriber;
    subscribeToEvents();
  }

  public void subscribeToEvents() {
    shortenerSubscriber.eventsShortenerViewed()
        .flatMap(this::processViewedEvent)
        .subscribe();
    shortenerSubscriber.shortenerChanged()
        .flatMap(this::processShortenerChangedEvent)
        .subscribe();
  }

  private Mono<StatisticDocument> processViewedEvent(ShortenerViewed event) {
    return repository.findByEventId(event.eventId())
        .next()
        .switchIfEmpty(Mono.defer(() -> {
          StatisticDocument statisticDocument = new StatisticDocument();
          statisticDocument.setNumber(event.number());
          statisticDocument.setUrl(event.key());
          statisticDocument.setCreatedAt(event.eventCreatedAt());
          statisticDocument.setEventId(event.eventId());
          statisticDocument.setEventType(EventType.VIEWED);
          return  repository.save(statisticDocument);
        }));
  }

  private Mono<StatisticDocument> processShortenerChangedEvent(ShortenerChangedEvent event) {
    StatisticDocument statisticDocument = new StatisticDocument();
    statisticDocument.setNumber(1L);
    statisticDocument.setUrl(event.payload().key().get());
    statisticDocument.setCreatedAt(event.eventCreatedAt());
    statisticDocument.setEventId(event.eventId());
    switch (event.type()) {
      case CREATED:
        statisticDocument.setEventType(EventType.CREATED);
        break;
      case UPDATED:
        statisticDocument.setEventType(EventType.UPDATED);
        break;
      case DELETED:
        statisticDocument.setEventType(EventType.DELETED);
        break;
    }
    return repository.save(statisticDocument);
  }

  public Flux<StatisticDocument> getAll() {
    log.debug(() -> "getAll() ->");
    return repository.findAll()
        .takeLast(100);
  }

  public Mono<Long> numberOfViews(String key) {
    log.debug(() -> "numberOfViews() ->");
    return repository.findByUrlAndEventType(key, EventType.VIEWED)
        .map(StatisticDocument::getNumber)
        .reduce(0L,(num1, num2) -> num1 + num2);
  }

  @Override
  public Mono<Long> eventsCount(EventType type) {
    return repository.findByEventType(type).map(r -> 1).reduce(0L, (n1, n2) -> n1+n2);
  }

}
