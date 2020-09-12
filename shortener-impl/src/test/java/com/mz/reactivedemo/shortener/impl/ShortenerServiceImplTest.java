package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.adapter.persistance.document.DocumentReadOnlyRepository;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.user.dto.UserDto;
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
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Created by zemi on 29/05/2018.
 */

@ExtendWith(MockitoExtension.class)
public class ShortenerServiceImplTest {

  @Mock
  AggregateService<ShortenerDto> aggregateRepository;

  @Mock
  DocumentReadOnlyRepository<String, UserDto> userDocumentReadOnlyRepository;

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

    var state = ShortenerDto.builder()
        .id(id)
        .url(url)
        .key(key)
        .shortUrl(shortUrl)
        .createdAt(now)
        .version(1L)
        .build();
    var user = UserDto.builder()
        .id(UUID.randomUUID().toString())
        .version(1L)
        .createdAt(Instant.now())
        .firstName("FirstName")
        .lastName("LastName")
        .build();

    Mockito.when(aggregateRepository.execute(any(String.class), any(CreateShortener.class))).thenReturn(Mono.just(state));
    Mockito.when(userDocumentReadOnlyRepository.get(anyString())).thenReturn(Mono.just(user));

    Mono<ShortenerDto> source = stub.create(createShortener);

    StepVerifier.create(source)
        .expectNextMatches(nextValue -> url.equals(nextValue.url()))
        .expectComplete().verify();
  }
}
