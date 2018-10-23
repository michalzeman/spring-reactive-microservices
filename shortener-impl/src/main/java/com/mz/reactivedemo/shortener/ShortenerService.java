package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import com.mz.reactivedemo.shortener.api.events.ShortenerEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by zemi on 29/05/2018.
 */
public interface ShortenerService {

  Mono<ShortenerDTO> create(CreateShortener shortener);

  Mono<ShortenerDTO> update(UpdateShortener shortener);

  Flux<ShortenerDTO> getAll();

  Flux<ShortenerEvent> events();

  Flux<ShortenerDTO> documents();

  Mono<ShortenerDTO> get(String id);

  Mono<String> map(String key);
}
