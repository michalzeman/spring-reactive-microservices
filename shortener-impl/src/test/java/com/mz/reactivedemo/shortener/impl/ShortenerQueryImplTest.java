package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.shortener.ShortenerRepository;
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

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ShortenerQueryImplTest {

  @Mock
  ShortenerRepository repository;

  @Mock
  ApplicationMessageBus messageBus;

  @InjectMocks
  ShortenerQueryImpl shortenerQuery;

  @Test
  void map() {
    final String key = "14e9c9c8-e23d-406a-bab6-5566358300a9";

    ShortenerDocument shortenerDocument = new ShortenerDocument();
    shortenerDocument.setKey(key);
    shortenerDocument.setUrl("www.url.tst");
    shortenerDocument.setShortUrl("www.url.tst");
    shortenerDocument.setId(UUID.randomUUID().toString());

    Mockito.when(repository.findByKey(key)).thenReturn(Mono.just(shortenerDocument));
    Mono<String> source = shortenerQuery.map(key);
    StepVerifier.create(source)
        .expectNext("http://www.url.tst")
        .expectComplete().verify();
  }
}