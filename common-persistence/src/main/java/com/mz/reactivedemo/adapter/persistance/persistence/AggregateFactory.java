package com.mz.reactivedemo.adapter.persistance.persistence;

import com.mz.reactivedemo.common.aggregate.Aggregate;

public interface AggregateFactory<S> {

  Aggregate<S> of(String id);

  Aggregate<S> of(S state);

}
