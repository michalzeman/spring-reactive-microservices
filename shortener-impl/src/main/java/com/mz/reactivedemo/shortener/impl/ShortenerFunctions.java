package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.common.api.events.DomainEvent;
import com.mz.reactivedemo.common.util.Match;
import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerEventType;
import com.mz.reactivedemo.shortener.domain.event.ShortenerCreated;
import com.mz.reactivedemo.shortener.domain.event.ShortenerUpdated;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.mz.reactivedemo.shortener.ShortenerMapper.*;

public interface ShortenerFunctions {

  @Component
  class UpdateView implements Function<ShortenerDto, Mono<ShortenerDto>> {

    private final ShortenerRepository repository;

    public UpdateView(ShortenerRepository repository) {
      this.repository = repository;
    }

    @Override
    public Mono<ShortenerDto> apply(ShortenerDto shortenerDto) {
      return repository.save(mapToDocument.apply(shortenerDto)).map(mapToDTO);
    }
  }

  @Component
  class PublishChangedEvent implements Consumer<DomainEvent> {

    private final ApplicationMessageBus applicationMessageBus;

    public PublishChangedEvent(ApplicationMessageBus applicationMessageBus) {
      this.applicationMessageBus = applicationMessageBus;
    }

    @Override
    public void accept(DomainEvent event) {
      Match.<ShortenerChangedEvent>match(event)
          .when(ShortenerCreated.class, e -> ShortenerChangedEvent.builder()
              .aggregateId(e.aggregateId())
              .payload(mapDtoToPayload.apply(e.shortener()))
              .type(ShortenerEventType.CREATED)
              .build())
          .when(ShortenerUpdated.class, mapUpdatedToChangedEvent)
          .get().ifPresent(applicationMessageBus::publishEvent);
    }
  }

  @Component
  class PublishDocumentMessage implements Consumer<ShortenerDto> {

    private final ApplicationMessageBus applicationMessageBus;

    public PublishDocumentMessage(ApplicationMessageBus applicationMessageBus) {
      this.applicationMessageBus = applicationMessageBus;
    }

    @Override
    public void accept(ShortenerDto shortenerDto) {
      this.applicationMessageBus.publishShortenerDto(shortenerDto);
    }
  }
}
