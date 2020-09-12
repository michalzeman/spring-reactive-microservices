package com.mz.reactivedemo.adapter.persistance.document;

import reactor.core.publisher.Mono;

public interface DocumentReadOnlyRepository<K, V> {

  Mono<V> get(K key);

}
