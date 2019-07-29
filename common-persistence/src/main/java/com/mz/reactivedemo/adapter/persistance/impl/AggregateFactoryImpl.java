package com.mz.reactivedemo.adapter.persistance.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.common.aggregate.Aggregate;

import java.util.function.Function;

public class AggregateFactoryImpl<S> implements AggregateFactory<S> {

  private final Function<String, Aggregate<S>> createAggregateById;

  private final Function<S, Aggregate<S>> createAggregateByState;

  public AggregateFactoryImpl(Function<String, Aggregate<S>> createAggregateById,
                              Function<S, Aggregate<S>> createAggregateByState) {
    this.createAggregateById = createAggregateById;
    this.createAggregateByState = createAggregateByState;
  }

  @Override
  public Aggregate<S> of(String id) {
    return createAggregateById.apply(id);
  }

  @Override
  public Aggregate<S> of(S state) {
    return createAggregateByState.apply(state);
  }
}
