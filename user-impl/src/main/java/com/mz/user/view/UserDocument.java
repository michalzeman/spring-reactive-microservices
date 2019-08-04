package com.mz.user.view;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Created by zemi on 02/01/2019.
 */
@Document(collection = "user")
public class UserDocument {

  @Id
  private String id;

  private Long version;

  private String firstName;

  private String lastName;

  private Instant createdAt;

  private List<String> shortenerIds;

  private ContactInfoDocument contactInformationDocument;

  public UserDocument() {
    super();
  }

  public UserDocument(String id, String firstName, String lastName, Long version, Instant createdAt,
                      ContactInfoDocument contactInformationDocument) {
    this.id = id;
    this.version = version;
    this.firstName = firstName;
    this.lastName = lastName;
    this.createdAt = createdAt;
    this.contactInformationDocument = contactInformationDocument;
  }

  public List<String> getShortenerIds() {
    return shortenerIds;
  }

  public void setShortenerIds(List<String> shortenerIds) {
    this.shortenerIds = shortenerIds;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
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

  public ContactInfoDocument getContactInformationDocument() {
    return contactInformationDocument;
  }

  public void setContactInformationDocument(ContactInfoDocument contactInformationDocument) {
    this.contactInformationDocument = contactInformationDocument;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserDocument)) return false;
    UserDocument that = (UserDocument) o;
    return id.equals(that.id) &&
        version.equals(that.version) &&
        firstName.equals(that.firstName) &&
        lastName.equals(that.lastName) &&
        createdAt.equals(that.createdAt) &&
        shortenerIds.equals(that.shortenerIds) &&
        contactInformationDocument.equals(that.contactInformationDocument);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, version, firstName, lastName, createdAt, shortenerIds, contactInformationDocument);
  }
}
