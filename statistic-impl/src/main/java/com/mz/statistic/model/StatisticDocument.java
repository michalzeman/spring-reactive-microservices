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

  private String url;

  private Long number;

  private String eventId;

  private Instant createdAt;

  private EventType eventType;

  public StatisticDocument(String id, String url, Long number, String eventId, Instant createdAt, EventType eventType) {
    this.id = id;
    this.url = url;
    this.number = number;
    this.eventId = eventId;
    this.createdAt = createdAt;
    this.eventType = eventType;
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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof StatisticDocument)) return false;
    StatisticDocument that = (StatisticDocument) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(url, that.url) &&
        Objects.equals(number, that.number) &&
        Objects.equals(eventId, that.eventId) &&
        Objects.equals(createdAt, that.createdAt) &&
        eventType == that.eventType;
  }

  @Override
  public int hashCode() {

    return Objects.hash(id, url, number, eventId, createdAt, eventType);
  }
}
