package com.mz.statistic.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

/**
 * Created by zemi on 29/05/2018.
 */
@Document(collection = "statistic")
public class StatisticDocument {

  @Id
  private String id;

  private String eventId;

  private Instant createdAt;

  private EventType eventType;

  private String aggregateId;

  public StatisticDocument(String id, String eventId, Instant createdAt, EventType eventType, String aggregateId) {
    this.id = id;
    this.eventId = eventId;
    this.createdAt = createdAt;
    this.eventType = eventType;
    this.aggregateId = aggregateId;
  }

  public StatisticDocument() {
  }

  public String getEventId() {

    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public void setAggregateId(String aggregateId) {
    this.aggregateId = aggregateId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StatisticDocument that = (StatisticDocument) o;
    return id.equals(that.id) &&
        eventId.equals(that.eventId) &&
        createdAt.equals(that.createdAt) &&
        eventType == that.eventType &&
        aggregateId.equals(that.aggregateId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventId, createdAt, eventType, aggregateId);
  }
}
