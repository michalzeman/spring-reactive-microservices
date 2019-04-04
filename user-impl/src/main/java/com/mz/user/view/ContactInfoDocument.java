package com.mz.user.view;

import java.time.Instant;
import java.util.Objects;

/**
 * Created by zemi on 02/01/2019.
 */
public class ContactInfoDocument {

  private String email;

  private String phoneNumber;

  private Instant createdAt;

  public ContactInfoDocument() {
  }

  public ContactInfoDocument(String email, String phoneNumber, Instant createdAt) {
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.createdAt = createdAt;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
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
    if (!(o instanceof ContactInfoDocument)) return false;
    ContactInfoDocument that = (ContactInfoDocument) o;
    return Objects.equals(email, that.email) &&
        Objects.equals(phoneNumber, that.phoneNumber) &&
        Objects.equals(createdAt, that.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, phoneNumber, createdAt);
  }
}
