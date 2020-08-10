package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import reactor.core.publisher.Mono;

/**
 * Created by zemi on 29/05/2018.
 */
public interface ShortenerService {

  Mono<ShortenerDto> create(CreateShortener shortener);

  Mono<ShortenerDto> update(UpdateShortener shortener);

//  Flux<ShortenerDto> getAll();
//
//  Flux<Event> events();
//
//  Flux<ShortenerDto> documents();
//
//  Mono<ShortenerDto> get(String eventId);
//
//  Mono<String> map(String key);
}
