package com.mz.reactivedemo.adapter.persistance.document.impl;

import com.mz.reactivedemo.adapter.persistance.document.DocumentReadOnlyRepository;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class DocumentReadOnlyRepositoryImpl<K, V> implements DocumentReadOnlyRepository<K, V> {

  private final InteractiveQueryService queryService;

  private final Scheduler scheduler;

  private final String documentStorageName;

  public DocumentReadOnlyRepositoryImpl(InteractiveQueryService queryService, Scheduler scheduler, String documentStorageName) {
    this.queryService = requireNonNull(queryService, "queryService is required");
    this.scheduler = requireNonNull(scheduler, "scheduler is required");
    this.documentStorageName = requireNonNull(documentStorageName, "documentStorageName is required");
  }

  @Override
  public Mono<V> get(K key) {
    return Mono.fromCallable(this::getReadOnlyKeyValueStore)
        .flatMap(getValue(key))
        .publishOn(scheduler);
  }

  private Function<ReadOnlyKeyValueStore<K, V>, Mono<V>> getValue(K key) {
    return storage -> Mono.justOrEmpty(Optional.ofNullable(key).map(storage::get));
  }

  private ReadOnlyKeyValueStore<K, V> getReadOnlyKeyValueStore() {
    return queryService.getQueryableStore(documentStorageName, QueryableStoreTypes.keyValueStore());
  }
}
