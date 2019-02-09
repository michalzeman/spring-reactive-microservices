package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShortenerQuery {

  Flux<ShortenerDto> getAll();

  Mono<ShortenerDto> get(String id);

  Mono<String> map(String key);

}
