package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerEventType;
import com.mz.reactivedemo.shortener.api.event.ShortenerPayload;
import com.mz.reactivedemo.shortener.domain.event.ShortenerUpdated;
import com.mz.reactivedemo.shortener.view.ShortenerDocument;

import java.util.Optional;
import java.util.function.Function;

public enum ShortenerMapper {

  FN;

  public final Function<ShortenerDocument, ShortenerDto> mapToDTO = document -> ShortenerDto.builder()
      .id(document.getId())
      .key(document.getKey())
      .url(document.getUrl())
      .shortUrl(document.getShortUrl())
      .userId(Optional.ofNullable(document.getUserId()))
      .createdAt(document.getCreatedAt())
      .version(document.getVersion())
      .build();

  public final Function<ShortenerDto, ShortenerDocument> mapToDocument = dto -> {
    var document =
        new ShortenerDocument(dto.key(), dto.url(), dto.shortUrl(), dto
            .createdAt(), dto.version());
    document.setId(dto.id());
    dto.userId().ifPresent(document::setUserId);
    return document;
  };

  public final Function<ShortenerUpdated, ShortenerChangedEvent> mapUpdatedToChangedEvent = updated ->
      ShortenerChangedEvent.builder()
          .aggregateId(updated.aggregateId())
          .payload(ShortenerPayload.builder()
              .id(updated.aggregateId())
              .url(updated.url())
              .version(updated.version())
              .build())
          .type(ShortenerEventType.UPDATED)
          .build();

  public final Function<ShortenerDto, ShortenerPayload> mapDtoToPayload = dto -> ShortenerPayload.builder()
      .id(dto.id())
      .key(dto.key())
      .url(dto.url())
      .userId(dto.userId())
      .shortUrl(dto.shortUrl())
      .version(dto.version())
      .build();

}
