package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by zemi on 29/05/2018.
 */

@ExtendWith(MockitoExtension.class)
public class ShortenerServiceImplTest {

  @Mock
  AggregateService<ShortenerDto> aggregateRepository;

  @InjectMocks
  ShortenerApplicationServiceImpl stub;

  @Test
  public void create() {

    String id = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();

    String url = "http://testlong.test";
    CreateShortener createShortener = CreateShortener.builder()
        .url(url)
        .userId(userId)
        .build();

    String key = UUID.randomUUID().toString();
    String shortUrl = "http://testurl.org";
    Instant now = Instant.now();

    ShortenerDto state = ShortenerDto.builder()
        .id(id)
        .url(url)
        .key(key)
        .shortUrl(shortUrl)
        .createdAt(now)
        .version(1L)
        .build();

    Mockito.when(aggregateRepository.execute(any(String.class), any(CreateShortener.class))).thenReturn(Mono.just(state));
    Mono<ShortenerDto> source = stub.create(createShortener);

    StepVerifier.create(source)
        .expectNextMatches(nextValue -> url.equals(nextValue.url()))
        .expectComplete().verify();
  }
}
