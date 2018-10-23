package com.mz.reactivedemo.shortener.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

/**
 * Created by zemi on 29/05/2018.
 */
@Document(collection = "shortener")
public class ShortenerDocument {

  @Id
  private String id;

  private String key;

  private String url;

  private String shortUrl;

  private Instant createdAt;

  @Version
  private Long version;

  /**
   *
   * @param key
   * @param url
   * @param shortUrl
   * @param createdAt
   */
  public ShortenerDocument(String key, String url, String shortUrl, Instant createdAt, Long version) {
    this.key = key;
    this.url = url;
    this.shortUrl = shortUrl;
    this.createdAt = createdAt;
    this.version = version;
  }

  public ShortenerDocument() {
  }

  public String getShortUrl() {
    return shortUrl;
  }

  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ShortenerDocument)) return false;
    ShortenerDocument that = (ShortenerDocument) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(key, that.key) &&
        Objects.equals(url, that.url) &&
        Objects.equals(shortUrl, that.shortUrl) &&
        Objects.equals(createdAt, that.createdAt) &&
        Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {

    return Objects.hash(id, key, url, shortUrl, createdAt, version);
  }
}
