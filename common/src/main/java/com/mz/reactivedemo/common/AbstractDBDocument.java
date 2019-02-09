package com.mz.reactivedemo.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.Objects;

/**
 * Created by zemi on 2019-01-20.
 */
public abstract class AbstractDBDocument {
  @Id
  private String id;

  @Version
  private Long version;

  private Instant createdAt;

  public AbstractDBDocument(Instant createdAt, Long version) {
    this.createdAt = createdAt;
    this.version = version;
  }

  public AbstractDBDocument() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractDBDocument)) return false;
    AbstractDBDocument that = (AbstractDBDocument) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(version, that.version) &&
        Objects.equals(createdAt, that.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, version, createdAt);
  }
}
