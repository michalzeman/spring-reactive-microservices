package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.adapter.persistance.persistence.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.persistence.PersistenceRepository;
import com.mz.reactivedemo.common.CommandResult;
import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.event.ShortenerCreated;
import com.mz.reactivedemo.shortener.streams.ApplicationMessageBus;
import com.mz.reactivedemo.shortener.view.ShortenerDocument;
import org.eclipse.collections.impl.factory.Lists;
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
  ShortenerRepository repository;

  @Mock
  ApplicationMessageBus messageBus;

  @Mock
  PersistenceRepository persistenceRepository;

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
    String key = UUID.randomUUID().toString();
    shortenerDocument.setKey(key);
    shortenerDocument.setUrl(url);
    shortenerDocument.setId(id);
    String shortUrl = "http://testurl.org";
    shortenerDocument.setShortUrl(shortUrl);
    Instant now = Instant.now();
    shortenerDocument.setCreatedAt(now);
    shortenerDocument.setVersion(1L);

    ShortenerDto state = ShortenerDto.builder()
        .id(id)
        .url(url)
        .key(key)
        .shortUrl(shortUrl)
        .createdAt(now)
        .version(1L)
        .build();

    CommandResult<ShortenerDto> commandResult = CommandResult.of(state,
        Lists.immutable.of(ShortenerCreated.builder().shortener(state).build()));

    Mockito.when(persistenceRepository.execute(any(String.class), any(CreateShortener.class),
        any(AggregateFactory.class))).thenReturn(Mono.just(commandResult));
    Mockito.when(repository.save(any(ShortenerDocument.class))).thenReturn(Mono.just(shortenerDocument));
    Mono<ShortenerDto> source = stub.create(createShortener);

    StepVerifier.create(source)
        .expectNextMatches(nextValue -> url.equals(nextValue.url()))
        .expectComplete().verify();
  }
}
