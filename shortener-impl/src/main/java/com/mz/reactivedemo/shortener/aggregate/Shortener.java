package com.mz.reactivedemo.shortener.aggregate;

import com.mz.reactivedemo.common.ApplyResult;
import com.mz.reactivedemo.common.ImmutableApplyResult;
import com.mz.reactivedemo.common.events.Command;
import com.mz.reactivedemo.common.events.Event;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ImmutableShortenerDTO;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import com.mz.reactivedemo.shortener.events.ImmutableShortenerCreated;
import com.mz.reactivedemo.shortener.events.ImmutableShortenerUpdated;
import com.mz.reactivedemo.shortener.model.ShortenerDocument;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by zemi on 29/09/2018.
 */
public class Shortener {

  private Optional<IdValue> id = Optional.empty();

  private ShortUrlValue shortUrl;

  private StringValue key;

  private UrlValue url;

  private Instant createdAt;

  private long version = -1;

  private Shortener() {

  }

  private Shortener(ShortenerDocument shortenerDocument) {
    this.id = Optional.of(new IdValue(shortenerDocument.getId()));
    this.shortUrl = new ShortUrlValue(shortenerDocument.getShortUrl());
    this.url = new UrlValue(shortenerDocument.getUrl());
    this.key = new StringValue(shortenerDocument.getKey());
    this.createdAt = shortenerDocument.getCreatedAt();
    this.version = shortenerDocument.getVersion();
  }

  private void create(CreateShortener cmd) {
    this.id = Optional.of(new IdValue(UUID.randomUUID().toString()));
    this.url = new UrlValue(cmd.url());
    this.key = new StringValue(UUID.randomUUID().toString());
    this.shortUrl = new ShortUrlValue(this.key.value);
    this.createdAt = Instant.now();
  }

  private void update(UpdateShortener cmd) {
    this.url = new UrlValue(cmd.url());
  }

  public ApplyResult<ShortenerDocument> apply(Command cmd) {
    List<Event> events = new ArrayList<>();
    if (cmd instanceof CreateShortener) {
      create((CreateShortener) cmd);
      events.add(ImmutableShortenerCreated.builder().shortener(toDto()).build());
    } else if (cmd instanceof UpdateShortener) {
      update((UpdateShortener) cmd);
      events.add(ImmutableShortenerUpdated.builder().shortener(toDto()).build());
    }
    return ImmutableApplyResult.<ShortenerDocument>builder().events(events).result(toDocument()).build();
  }

  public ShortenerDocument toDocument() {
    ShortenerDocument document = new ShortenerDocument(this.key.value, this.url.value, this.shortUrl.value, this
        .createdAt, (version == -1) ? null : version);
    this.id.ifPresent(id -> document.setId(id.value));
    return document;
  }

  public ShortenerDTO toDto() {
    ImmutableShortenerDTO.Builder builder = ShortenerDTO.builder()
        .key(this.key.value)
        .url(this.url.value)
        .shortUrl(this.shortUrl.value)
        .version(Optional.ofNullable((this.version == 0) ? null : this.version))
        .createdAt(this.createdAt);
    this.id.ifPresent(id -> builder.id(id.value));

    return builder.build();
  }

  public static Shortener of(ShortenerDocument shortenerDocument) {
    return new Shortener(shortenerDocument);
  }

  public static Shortener of() {
    return new Shortener();
  }
}
