package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.utils.PatternMatching;
import com.mz.reactivedemo.common.services.AbstractApplicationService;
import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.ShortenerService;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.events.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.events.ShortenerEventType;
import com.mz.reactivedemo.shortener.domain.aggregate.ShortenerEntity;
import com.mz.reactivedemo.shortener.domain.events.ShortenerCreated;
import com.mz.reactivedemo.shortener.domain.events.ShortenerUpdated;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.mz.reactivedemo.shortener.ShortenerFunctions.mapToDTO;
import static com.mz.reactivedemo.shortener.ShortenerFunctions.mapToDocument;

/**
 * Created by zemi on 29/05/2018.
 */
@Service
public class ShortenerApplicationServiceImpl extends AbstractApplicationService<ShortenerDto, ShortenerChangedEvent> implements ShortenerService {

  private static final Log log = LogFactory.getLog(ShortenerApplicationServiceImpl.class);

  private final ApplicationMessageBus applicationMessageBus;

  private final ShortenerRepository repository;

  public ShortenerApplicationServiceImpl(ShortenerRepository repository, ApplicationMessageBus applicationMessageBus) {
    this.applicationMessageBus = applicationMessageBus;
    this.repository = repository;
  }

  @Override
  protected Optional<ShortenerChangedEvent> mapToChangedEvent(Event event, ShortenerDto shortenerDto) {
    return PatternMatching.<ShortenerChangedEvent>match(event)
        .when(ShortenerCreated.class, e -> ShortenerChangedEvent.builder()
            .payload(shortenerDto)
            .type(ShortenerEventType.CREATED)
            .build())
        .when(ShortenerUpdated.class, e -> ShortenerChangedEvent.builder()
            .payload(shortenerDto)
            .type(ShortenerEventType.CREATED)
            .build())
        .result();
  }

  @Override
  protected void publishChangedEvent(Event event) {
    applicationMessageBus.publishEvent(event);
  }

  @Override
  protected void publishDocumentMessage(ShortenerDto doc) {
    applicationMessageBus.publishShortenerDto(doc);
  }

  @Override
  protected Mono<ShortenerDto> applyToStorage(ApplyResult<ShortenerDto> result) {
    return repository.save(mapToDocument.apply(result.result()))
        .map(mapToDTO);
  }

  @Override
  public Mono<ShortenerDto> create(CreateShortener createShortener) {
    log.debug("create() ->");
    return processChanges(Mono.just(createShortener)
        .map(cmd -> ShortenerEntity.of().apply(cmd)));
  }

  @Override
  public Mono<ShortenerDto> update(UpdateShortener shortener) {
    log.debug("update() ->");
    return processChanges(repository.findById(shortener.id())
        .map(mapToDTO)
        .map(d -> ShortenerEntity.of(d).apply(shortener)));

  }
}
