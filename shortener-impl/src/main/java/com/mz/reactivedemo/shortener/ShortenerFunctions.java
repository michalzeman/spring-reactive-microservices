package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.events.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.events.ShortenerEventType;
import com.mz.reactivedemo.shortener.api.events.ShortenerPayload;
import com.mz.reactivedemo.shortener.domain.aggregate.ShortenerState;
import com.mz.reactivedemo.shortener.domain.events.ShortenerUpdated;
import com.mz.reactivedemo.shortener.model.ShortenerDocument;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ShortenerFunctions {

  Function<ShortenerDocument, ShortenerDto> mapToDTO = document -> ShortenerDto.builder()
      .id(document.getId())
      .key(document.getKey())
      .url(document.getUrl())
      .shortUrl(document.getShortUrl())
      .createdAt(document.getCreatedAt())
      .version(document.getVersion())
      .build();

  Function<ShortenerDto, ShortenerDocument> mapToDocument = dto -> {
    ShortenerDocument document =
        new ShortenerDocument(dto.key(), dto.url(), dto.shortUrl(), dto
            .createdAt(), dto.version());
    document.setId(dto.id());
    return document;
  };

  Function<ShortenerState, ShortenerDocument> mapStateToDocument = state -> {
    ShortenerDocument document =
        new ShortenerDocument(state.key(), state.url(), state.shortUrl(), state
            .createdAt(), state.version().orElse(null));
    state.id().ifPresent(document::setId);
    return document;
  };

  BiFunction<ShortenerUpdated, ShortenerPayload, ShortenerChangedEvent> mapUpdatedToChangedEvent = (updated, payload) ->
      ShortenerChangedEvent.builder()
          .payload(ShortenerPayload.builder()
              .id(payload.id())
              .url(updated.url())
              .version(payload.version())
              .build())
          .type(ShortenerEventType.UPDATED)
          .build();

  Function<ShortenerDto, ShortenerPayload> mapDtoToPayload = dto -> ShortenerPayload.builder()
      .id(dto.id())
      .key(dto.key())
      .url(dto.url())
      .shortUrl(dto.shortUrl())
      .version(dto.version())
      .build();

}
