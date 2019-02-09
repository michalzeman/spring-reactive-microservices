package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.events.ShortenerEvent;
import reactor.core.publisher.Flux;
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
//  Mono<ShortenerDto> get(String id);
//
//  Mono<String> map(String key);
}
