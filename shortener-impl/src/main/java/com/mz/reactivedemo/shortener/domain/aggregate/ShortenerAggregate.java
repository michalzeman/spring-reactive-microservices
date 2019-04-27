package com.mz.reactivedemo.shortener.domain.aggregate;

import com.mz.reactivedemo.common.aggregate.*;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.DomainEvent;
import com.mz.reactivedemo.common.api.util.CaseMatch;
import com.mz.reactivedemo.common.api.util.Match;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.event.ShortenerCreated;
import com.mz.reactivedemo.shortener.domain.event.ShortenerUpdated;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by zemi on 29/09/2018.
 */
public class ShortenerAggregate extends AbstractRootAggregate<ShortenerDto> {

  private Id id;

  private Id userId;

  private ShortUrl shortUrl;

  private StringValue key;

  private Url url;

  private Instant createdAt;

  private Long version;

  private ShortenerAggregate(String id) {
    this.id = new Id(id);
    this.version = 0L;
    this.status = AggregateStatus.NEW;
  }

  private ShortenerAggregate(ShortenerDto shortenerState) {
    this(shortenerState.id());
    this.shortUrl = new ShortUrl(shortenerState.shortUrl());
    this.url = new Url(shortenerState.url());
    this.key = new StringValue(shortenerState.key());
    this.createdAt = shortenerState.createdAt();
    this.version = shortenerState.version();
    shortenerState.userId().ifPresent(userId -> this.userId = new Id(userId));
    this.status = AggregateStatus.EXISTING;
  }

  private ShortenerCreated validateCreate(CreateShortener cmd) {
    StringValue key = new StringValue(UUID.randomUUID().toString());
    Url url = new Url(cmd.url());
    ShortUrl shortUrl = new ShortUrl(key.value);
    Id userId = new Id(cmd.userId());
    ShortenerDto state = ShortenerDto.builder()
        .id(this.id.value)
        .version(0L)
        .createdAt(Instant.now())
        .key(key.value)
        .shortUrl(shortUrl.value)
        .url(url.value)
        .userId(userId.value)
        .build();
    return ShortenerCreated.builder().shortener(state).aggregateId(this.id.value).build();
  }

  private ShortenerUpdated validateUpdate(UpdateShortener cmd) {
    if (status == AggregateStatus.NEW) {
      throw new RuntimeException("Wrong aggregate status");
    }
    Url url = new Url(cmd.url());
    return ShortenerUpdated.builder().aggregateId(this.id.value).url(url.value).version(this.version).build();
  }

  private void applyShortenerUpdated(ShortenerUpdated evt) {
    this.url = new Url(evt.url());
    ++this.version;
  }

  private void applyShortenerCreated(ShortenerCreated evt) {
    this.url = new Url(evt.shortener().url());
    this.key = new StringValue(evt.shortener().key());
    this.shortUrl = new ShortUrl(this.key.value);
    this.createdAt = evt.eventCreatedAt();
    evt.shortener().userId().ifPresent(userId -> this.userId = new Id(userId));
    this.status = AggregateStatus.EXISTING;
  }

  @Override
  protected ImmutableList<DomainEvent> behavior(Command cmd) {
    return Match.<DomainEvent>match(cmd)
        .when(CreateShortener.class, this::validateCreate)
        .when(UpdateShortener.class, this::validateUpdate)
        .get().map(Lists.immutable::of).orElseGet(Lists.immutable::empty);
  }

  @Override
  protected String getRootEntityId() {
    return id.value;
  }

  @Override
  public Aggregate<ShortenerDto> apply(DomainEvent event) {
    CaseMatch.match(event)
        .when(ShortenerCreated.class, this::applyShortenerCreated)
        .when(ShortenerUpdated.class, this::applyShortenerUpdated);
    return this;
  }

  @Override
  public ShortenerDto state() {
    return ShortenerDto.builder()
        .id(id.value)
        .key(this.key.value)
        .url(this.url.value)
        .shortUrl(this.shortUrl.value)
        .version(this.version)
        .userId(this.userId.value)
        .createdAt(this.createdAt).build();
  }

  public static ShortenerAggregate of(ShortenerDto shortenerDto) {
    return new ShortenerAggregate(shortenerDto);
  }

  public static ShortenerAggregate of(String id) {
    return new ShortenerAggregate(id);
  }
}
