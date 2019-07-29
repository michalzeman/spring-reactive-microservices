package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerEventType;
import com.mz.reactivedemo.shortener.api.event.ShortenerPayload;
import com.mz.reactivedemo.shortener.domain.event.ShortenerUpdated;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortenerMapperTest {

  @Test
  void mapUpdateToChangedEvent() {
    String shortenerId = UUID.randomUUID().toString();
    ShortenerUpdated updated = ShortenerUpdated.builder()
        .aggregateId(shortenerId)
        .url("updatedUrl.com")
        .version(1L)
        .build();

    ShortenerPayload dto = ShortenerPayload.builder()
        .id(shortenerId)
        .version(1L)
        .url(updated.url())
        .build();

    ShortenerChangedEvent changedEvent= ShortenerMapper.mapUpdatedToChangedEvent.apply(updated);
    assertTrue(changedEvent.type() == ShortenerEventType.UPDATED);
    assertTrue(changedEvent.payload().id().equals(dto.id()));
    assertTrue(changedEvent.payload().version().equals(dto.version()));
    assertTrue(changedEvent.payload().url().equals(dto.url()));
    assertTrue(changedEvent.payload().url().equals(dto.url()));
  }

  @Test
  void mapDtoToPayload() {
    ShortenerDto dto = ShortenerDto.builder()
        .id(UUID.randomUUID().toString())
        .url("url")
        .shortUrl("shortUrl")
        .createdAt(Instant.now())
        .key(UUID.randomUUID().toString())
        .version(1L)
        .build();

    ShortenerPayload payload = ShortenerMapper.mapDtoToPayload.apply(dto);
    assertEquals(payload.id(), dto.id());
    assertEquals(payload.version(), dto.version());
    assertEquals(payload.url().get(), dto.url());
    assertEquals(payload.key().get(), dto.key());
    assertEquals(payload.shortUrl().get(), dto.shortUrl());
  }
}
