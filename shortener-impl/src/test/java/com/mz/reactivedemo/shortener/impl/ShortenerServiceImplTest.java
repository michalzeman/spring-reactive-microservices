package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.model.ShortenerDocument;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by zemi on 29/05/2018.
 */

@ExtendWith(MockitoExtension.class)
public class ShortenerServiceImplTest {

  @Mock
  ShortenerRepository repository;

  @Mock
  ApplicationMessageBus messageBus;

  @InjectMocks
  ShortenerApplicationServiceImpl stub;

  @Test
  public void create() {

    String id = UUID.randomUUID().toString();

    String url = "http://testlong.test";
    CreateShortener createShortener = CreateShortener.builder()
        .url(url)
        .build();

    ShortenerDocument shortenerDocument = new ShortenerDocument();
    shortenerDocument.setKey(UUID.randomUUID().toString());
    shortenerDocument.setUrl(url);
    shortenerDocument.setId(id);
    shortenerDocument.setShortUrl("http://testurl.org");
    shortenerDocument.setCreatedAt(Instant.now());
    shortenerDocument.setVersion(1L);

    Mockito.when(repository.save(any(ShortenerDocument.class))).thenReturn(Mono.just(shortenerDocument));
    Mono<ShortenerDto> source = stub.create(createShortener);

    StepVerifier.create(source)
        .expectNextMatches(nextValue -> url.equals(nextValue.url()))
        .expectComplete().verify();
  }
}