package com.mz.reactivedemo.shortener.streams;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import reactor.core.publisher.Flux;

public interface ApplicationMessageBus {

  void publishEvent(Event event);

  void publishShortenerDto(ShortenerDto dto);

  Flux<Event> events();

  Flux<ShortenerDto> documents();

}
