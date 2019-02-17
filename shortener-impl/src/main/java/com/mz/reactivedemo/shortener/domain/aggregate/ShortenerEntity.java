package com.mz.reactivedemo.shortener.domain.aggregate;

import com.mz.reactivedemo.common.aggregates.AbstractRootAggregate;
import com.mz.reactivedemo.common.aggregates.Id;
import com.mz.reactivedemo.common.aggregates.StringValue;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.shortener.api.commands.CreateShortener;
import com.mz.reactivedemo.shortener.api.commands.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ImmutableShortenerDto;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.events.ImmutableShortenerCreated;
import com.mz.reactivedemo.shortener.domain.events.ImmutableShortenerUpdated;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by zemi on 29/09/2018.
 */
public class ShortenerEntity extends AbstractRootAggregate<ShortenerDto> {

  private Optional<Id> id = Optional.empty();

  private ShortUrl shortUrl;

  private StringValue key;

  private Url url;

  private Instant createdAt;

  private Optional<Long> version = Optional.empty();

  private ShortenerEntity() {

  }

  private ShortenerEntity(ShortenerDto shortenerDto) {
    this.id = Optional.of(new Id(shortenerDto.id().get()));
    this.shortUrl = new ShortUrl(shortenerDto.shortUrl());
    this.url = new Url(shortenerDto.url());
    this.key = new StringValue(shortenerDto.key());
    this.createdAt = shortenerDto.createdAt();
    this.version = shortenerDto.version();
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
      return Optional.of(ImmutableShortenerUpdated.builder().shortener(toResult()).build());
    }
    return Optional.empty();
  }

  @Override
  protected ShortenerDto toResult() {
    ImmutableShortenerDto.Builder builder = com.mz.reactivedemo.shortener.api.dto.ShortenerDto.builder()
        .key(this.key.value)
        .url(this.url.value)
        .shortUrl(this.shortUrl.value)
        .version(this.version)
        .createdAt(this.createdAt);
    this.id.ifPresent(id -> builder.id(id.value));

    return builder.build();
  }

  public static ShortenerEntity of(ShortenerDto shortenerDto) {
    return new ShortenerEntity(shortenerDto);
  }

  public static ShortenerEntity of() {
    return new ShortenerEntity();
  }
}
