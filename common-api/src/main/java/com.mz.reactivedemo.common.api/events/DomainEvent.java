package com.mz.reactivedemo.common.api.events;

public interface DomainEvent extends Event {
  String aggregateId();
}
