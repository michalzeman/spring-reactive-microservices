package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.common.events.Event;
import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.ShortenerService;
import com.mz.reactivedemo.shortener.aggregate.Shortener;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ImmutableShortenerDTO;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import com.mz.reactivedemo.shortener.api.events.*;
import com.mz.reactivedemo.shortener.events.ShortenerCreated;
import com.mz.reactivedemo.shortener.events.ShortenerUpdated;
import com.mz.reactivedemo.shortener.model.ShortenerDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by zemi on 29/05/2018.
 */
@Service
public class ShortenerServiceImpl implements ShortenerService {

  private static final Log log = LogFactory.getLog(ShortenerServiceImpl.class);

  private final ShortenerRepository repository;

  private final ReplayProcessor<ShortenerEvent> events = ReplayProcessor.create(1);

  private final FluxSink<ShortenerEvent> eventSink = events.sink();

  private final ReplayProcessor<ShortenerDTO> documents = ReplayProcessor.create(1);

  private final FluxSink<ShortenerDTO> documentsSink = documents.sink();


  @Autowired
  public ShortenerServiceImpl(ShortenerRepository repository) {
    this.repository = repository;
  }

  private void publishEvents(Set<Event> events, ShortenerDocument doc) {
    events.forEach(e -> mapEvent(e, doc).ifPresent(eventSink::next));
  }

  private String mapShortenerToValue(@NotNull ShortenerDocument shortenerDocument) {
    log.debug("mapShortenerToValue() ->");
    return "http://" + shortenerDocument.getUrl();
  }

  private ShortenerDTO mapToDTO(ShortenerDocument document) {
    return ImmutableShortenerDTO.builder()
        .id(document.getId())
        .key(document.getKey())
        .url(document.getUrl())
        .shortUrl(document.getShortUrl())
        .createdAt(document.getCreatedAt())
        .version(document.getVersion())
        .build();
  }

  private <T> boolean casePattern(Object obj, Class<T> type) {
    return Optional.ofNullable(type)
        .flatMap(t -> Optional.ofNullable(obj).map(o -> t.isInstance(o))).orElse(false);
  }

  private Optional<ShortenerChangedEvent> mapEvent(Event event, ShortenerDocument document) {
    if (casePattern(event, ShortenerCreated.class)) {
      return Optional.of(ImmutableShortenerChangedEvent.builder()
          .payload(mapToDTO(document))
          .type(ShortenerEventType.CREATED)
          .build());
    } else if (casePattern(event, ShortenerUpdated.class)) {
      return Optional.of(ImmutableShortenerChangedEvent.builder()
          .payload(mapToDTO(document))
          .type(ShortenerEventType.UPDATED)
          .build());
    }
    return Optional.empty();

  }

  private void publishDocument(ShortenerDTO doc) {
    this.documentsSink.next(doc);
  }

  @Override
  public Mono<ShortenerDTO> create(CreateShortener createShortener) {
    log.debug("create() ->");
    return Mono.just(createShortener)
        .map(cmd -> Shortener.of().apply(cmd))
        .flatMap(r ->
            repository.save(r.result())
                .doOnSuccess(d -> publishEvents(r.events(), d))
                .map(this::mapToDTO)
        ).doOnSuccess(this::publishDocument);
  }

  @Override
  public Mono<ShortenerDTO> update(UpdateShortener shortener) {
    log.debug("update() ->");
    return repository.findById(shortener.id())
        .map(d -> Shortener.of(d).apply(shortener))
        .flatMap(r ->
            repository.save(r.result())
                .doOnSuccess(d -> publishEvents(r.events(), d))
                .map(this::mapToDTO)
        ).doOnSuccess(this::publishDocument);
  }

  @Override
  public Flux<ShortenerDTO> getAll() {
    log.debug("getAll() ->");
    return repository.findAll().map(this::mapToDTO);
  }

  @Override
  public Mono<ShortenerDTO> get(String id) {
    log.debug("get() ->");
    return repository.findById(id)
        .map(this::mapToDTO);
  }

  @Override
  public Mono<String> map(String key) {
    log.debug("mapEvent() -> key: " + key);
    return repository.findByKey(key)
        .doOnSuccess(shortener -> Optional.ofNullable(shortener)
            .ifPresent(s -> eventSink.next(ImmutableShortenerViewed.builder()
                .key(s.getKey())
                .number(1L)
                .createdAt(Instant.now())
                .id(UUID.randomUUID().toString())
                .build())))
        .map(this::mapShortenerToValue);
  }

  @Override
  public Flux<ShortenerEvent> events() {
    return events.publishOn(Schedulers.parallel());
  }

  @Override
  public Flux<ShortenerDTO> documents() {
    return documents.publishOn(Schedulers.parallel());
  }
}
