package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.adapter.persistance.persistence.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.persistence.PersistenceRepository;
import com.mz.reactivedemo.common.api.events.DomainEvent;
import com.mz.reactivedemo.common.api.util.Match;
import com.mz.reactivedemo.common.service.ApplicationService;
import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.ShortenerService;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerEventType;
import com.mz.reactivedemo.shortener.domain.aggregate.ShortenerAggregate;
import com.mz.reactivedemo.shortener.domain.event.ShortenerCreated;
import com.mz.reactivedemo.shortener.domain.event.ShortenerUpdated;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.mz.reactivedemo.shortener.ShortenerFunctions.*;

/**
 * Created by zemi on 29/05/2018.
 */
@Service
public class ShortenerApplicationServiceImpl implements ShortenerService {

  private static final Log log = LogFactory.getLog(ShortenerApplicationServiceImpl.class);

  private final ApplicationMessageBus applicationMessageBus;

  private final ShortenerRepository repository;

  private final PersistenceRepository persistenceRepository;

  private final AggregateFactory<ShortenerDto> aggregateFactory;

  private final ApplicationService<ShortenerDto> applicationService;

  public ShortenerApplicationServiceImpl(ShortenerRepository repository, ApplicationMessageBus applicationMessageBus,
                                         PersistenceRepository persistenceRepository) {
    this.applicationMessageBus = applicationMessageBus;
    this.repository = repository;
    this.persistenceRepository = persistenceRepository;
    this.aggregateFactory = AggregateFactory.build(ShortenerAggregate::of, ShortenerAggregate::of);
    this.applicationService = ApplicationService.<ShortenerDto>of(this::updateView, this::publishChangedEvent,
      this::publishDocumentMessage);
  }

  protected void publishChangedEvent(DomainEvent event) {
    Match.<ShortenerChangedEvent>match(event)
        .when(ShortenerCreated.class, e -> ShortenerChangedEvent.builder()
            .aggregateId(e.aggregateId())
            .payload(mapDtoToPayload.apply(e.shortener()))
            .type(ShortenerEventType.CREATED)
            .build())
        .when(ShortenerUpdated.class, mapUpdatedToChangedEvent)
        .get().ifPresent(applicationMessageBus::publishEvent);
  }

  protected void publishDocumentMessage(ShortenerDto doc) {
    applicationMessageBus.publishShortenerDto(doc);
  }

  private Mono<ShortenerDto> updateView(ShortenerDto dto) {
    return repository.save(mapToDocument.apply(dto)).map(mapToDTO);
  }

  @Override
  public Mono<ShortenerDto> create(CreateShortener createShortener) {
    log.debug("execute() ->");
    return persistenceRepository.execute(UUID.randomUUID().toString(), createShortener, aggregateFactory)
        .flatMap(applicationService::processResult);
  }

  @Override
  public Mono<ShortenerDto> update(UpdateShortener shortener) {
    log.debug("update() ->");
    return persistenceRepository.execute(shortener.id(), shortener, aggregateFactory)
        .flatMap(applicationService::processResult);
  }

}
