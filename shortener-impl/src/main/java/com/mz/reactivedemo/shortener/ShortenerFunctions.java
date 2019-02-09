package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.model.ShortenerDocument;

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
            .createdAt(), dto.version().orElse(null));
    dto.id().ifPresent(id -> document.setId(id));
    return document;
  };

}
