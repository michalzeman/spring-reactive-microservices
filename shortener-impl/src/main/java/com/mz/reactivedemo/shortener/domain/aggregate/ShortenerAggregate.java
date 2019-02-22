package com.mz.reactivedemo.shortener.domain.aggregate;

import com.mz.reactivedemo.common.aggregates.AbstractRootAggregate;
import com.mz.reactivedemo.common.aggregates.Id;
import com.mz.reactivedemo.common.aggregates.StringValue;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.events.ImmutableShortenerCreated;
import com.mz.reactivedemo.shortener.domain.events.ImmutableShortenerUpdated;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by zemi on 29/09/2018.
 */
public class ShortenerAggregate extends AbstractRootAggregate<ShortenerState> {

  private Optional<Id> id = Optional.empty();

  private ShortUrl shortUrl;

  private StringValue key;

  private Url url;

  private Instant createdAt;

  private Optional<Long> version = Optional.empty();

  private ShortenerAggregate() {

  }

  private ShortenerAggregate(ShortenerDto shortenerDto) {
    this.id = Optional.of(new Id(shortenerDto.id()));
    this.shortUrl = new ShortUrl(shortenerDto.shortUrl());
    this.url = new Url(shortenerDto.url());
    this.key = new StringValue(shortenerDto.key());
    this.createdAt = shortenerDto.createdAt();
    this.version = Optional.of(shortenerDto.version());
  }

  private void create(CreateShortener cmd) {
    this.id = Optional.of(new Id(UUID.randomUUID().toString()));
    this.url = new Url(cmd.url());
    this.key = new StringValue(UUID.randomUUID().toString());
    this.shortUrl = new ShortUrl(this.key.value);
    this.createdAt = Instant.now();
  }

  private void update(UpdateShortener cmd) {
    this.url = new Url(cmd.url());
  }

  @Override
  protected Optional<Event> behavior(Command cmd) {
    if (cmd instanceof CreateShortener) {
      create((CreateShortener) cmd);
      return Optional.of(ImmutableShortenerCreated.builder().shortener(toResult()).build());
    } else if (cmd instanceof UpdateShortener) {
      update((UpdateShortener) cmd);
      return Optional.of(ImmutableShortenerUpdated.builder().shortenerId(this.id.get().value).url(this.url.value).build());
    }
    return Optional.empty();
  }

  @Override
  protected ShortenerState toResult() {
    return ShortenerState.builder()
        .id(id.map(i -> i.value))
        .key(this.key.value)
        .url(this.url.value)
        .shortUrl(this.shortUrl.value)
        .version(this.version)
        .createdAt(this.createdAt).build();
  }

  public static ShortenerAggregate of(ShortenerDto shortenerDto) {
    return new ShortenerAggregate(shortenerDto);
  }

  public static ShortenerAggregate of() {
    return new ShortenerAggregate();
  }
}
