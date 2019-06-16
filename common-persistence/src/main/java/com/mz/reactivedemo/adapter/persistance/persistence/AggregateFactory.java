package com.mz.reactivedemo.adapter.persistance.persistence;

import com.mz.reactivedemo.adapter.persistance.persistence.impl.AggregateFactoryImpl;
import com.mz.reactivedemo.common.aggregate.Aggregate;

import java.util.function.Function;

public interface AggregateFactory<S> {

  Aggregate<S> of(String id);

  Aggregate<S> of(S state);

  static <S> AggregateFactory<S> build(Function<String, Aggregate<S>> createAggregateById,
                            Function<S, Aggregate<S>> createAggregateByState) {
    return new AggregateFactoryImpl<S>(createAggregateById, createAggregateByState);
  }

}
