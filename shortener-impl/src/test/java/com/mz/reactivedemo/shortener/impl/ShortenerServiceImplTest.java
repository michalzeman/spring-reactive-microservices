package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.shortener.ShortenerRepository;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.ImmutableCreateShortener;
import com.mz.reactivedemo.shortener.api.dto.ImmutableShortenerDTO;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import com.mz.reactivedemo.shortener.model.ShortenerDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by zemi on 29/05/2018.
 */

@ExtendWith(SpringExtension.class)
public class ShortenerServiceImplTest {

  @Mock
  ShortenerRepository repository;

  @InjectMocks
  ShortenerServiceImpl stub;

  @Test
  public void create() {

    String id = UUID.randomUUID().toString();

    String url = "http://testlong.test";
    CreateShortener createShortener = ImmutableCreateShortener.builder()
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
    Mono<ShortenerDTO> source = stub.create(createShortener);

    StepVerifier.create(source)
        .expectNextMatches(nextValue -> url.equals(nextValue.url()))
        .expectComplete().verify();
  }

  @Test
  public void map() {
    final String key = "14e9c9c8-e23d-406a-bab6-5566358300a9";

    ShortenerDocument shortenerDocument = new ShortenerDocument();
    shortenerDocument.setKey(key);
    shortenerDocument.setUrl("www.url.tst");
    shortenerDocument.setShortUrl("www.url.tst");
    shortenerDocument.setId(UUID.randomUUID().toString());

    Mockito.when(repository.findByKey(key)).thenReturn(Mono.just(shortenerDocument));
    Mono<String> source = stub.map(key);
    StepVerifier.create(source)
        .expectNext("http://www.url.tst")
        .expectComplete().verify();
  }
}