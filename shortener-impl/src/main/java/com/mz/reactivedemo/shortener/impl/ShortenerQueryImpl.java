package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.shortener.ShortenerQuery;
import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.api.events.ShortenerViewed;
import com.mz.reactivedemo.shortener.model.ShortenerDocument;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import static com.mz.reactivedemo.shortener.ShortenerFunctions .*;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShortenerQueryImpl implements ShortenerQuery {

  private static final Log log = LogFactory.getLog(ShortenerApplicationServiceImpl.class);

  private final ShortenerRepository repository;

  private final ApplicationMessageBus shortenerMessageBus;

  private String mapShortenerToValue(@NotNull ShortenerDocument shortenerDocument) {
    log.debug("mapShortenerToValue() ->");
    return "http://" + shortenerDocument.getUrl();
  }

  public ShortenerQueryImpl(ShortenerRepository repository, ApplicationMessageBus shortenerMessageBus) {
    this.repository = repository;
    this.shortenerMessageBus = shortenerMessageBus;
  }


  @Override
  public Flux<com.mz.reactivedemo.shortener.api.dto.ShortenerDto> getAll() {
    log.debug("getAll() ->");
    return repository.findAll().map(mapToDTO);
  }

  @Override
  public Mono<com.mz.reactivedemo.shortener.api.dto.ShortenerDto> get(String id) {
    log.debug("get() ->");
    return repository.findById(id)
        .map(mapToDTO);
  }

  @Override
  public Mono<String> map(String key) {
    log.debug("mapToChangedEvent() -> key: " + key);
    return repository.findByKey(key)
        .doOnSuccess(shortener -> Optional.ofNullable(shortener)
            .ifPresent(s -> this.shortenerMessageBus.publishEvent(ShortenerViewed.builder()
                .key(s.getKey())
                .number(1L)
                .eventId(UUID.randomUUID().toString())
                .build())))
        .map(this::mapShortenerToValue);
  }

}
