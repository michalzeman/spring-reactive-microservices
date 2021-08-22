package com.mz.reactivedemo.shortener.streams;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
public class ApplicationMessageBusImpl implements ApplicationMessageBus {

  private static final Log log = LogFactory.getLog(ApplicationMessageBusImpl.class);

  protected final ReplayProcessor<Event> events = ReplayProcessor.create(1);

  protected final FluxSink<Event> eventSink = events.sink();

  protected final ReplayProcessor<ShortenerDto> documents = ReplayProcessor.create(1);

  protected final FluxSink<ShortenerDto> documentsSink = documents.sink();

  @Override
  public void publishEvent(Event event) {
    Optional.ofNullable(event).ifPresent(eventSink::next);
  }

  @Override
  public void publishShortenerDto(ShortenerDto dto) {
    Optional.ofNullable(dto).ifPresent(documentsSink::next);
  }

  @Override
  public Flux<Event> events() {
    return events.publishOn(Schedulers.parallel());
  }

  @Override
  public Flux<ShortenerDto> documents() {
    return documents.publishOn(Schedulers.parallel());
  }

}
