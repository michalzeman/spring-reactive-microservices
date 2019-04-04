package com.mz.user;

import com.mz.user.domain.event.ContactInfoCreated;
import com.mz.user.domain.event.UserCreated;
import com.mz.user.dto.ContactInfoDto;
import com.mz.user.dto.UserDto;
import com.mz.user.message.ContactInfoPayload;
import com.mz.user.message.UserPayload;
import com.mz.user.view.ContactInfoDocument;
import com.mz.user.view.UserDocument;

import java.util.Optional;
import java.util.function.Function;

public interface UserFunctions {

  Function<UserDocument, UserDto> mapToDto = doc ->
      UserDto.builder()
          .id(doc.getId())
          .firstName(doc.getFirstName())
          .lastName(doc.getLastName())
          .createdAt(doc.getCreatedAt())
          .version(doc.getVersion())
          .contactInformation(Optional.ofNullable(doc.getContactInformationDocument())
              .map(c -> ContactInfoDto.builder()
                  .userId(doc.getId())
                  .createdAt(c.getCreatedAt())
                  .email(Optional.ofNullable(c.getEmail()))
                  .phoneNumber(Optional.ofNullable(c.getPhoneNumber()))
                  .build()))
          .build();

  Function<UserDto, UserDocument> mapToDocument = dto ->
      new UserDocument(dto.id(), dto.firstName(),
          dto.lastName(), dto.version(), dto.createdAt(),
          dto.contactInformation().map(UserFunctions.mapToContactInfoDocument).orElse(null));

  Function<ContactInfoDto, ContactInfoDocument> mapToContactInfoDocument = dto ->
      new ContactInfoDocument(dto.email().orElse(null),
          dto.phoneNumber().orElse(null), dto.createdAt());

  Function<UserCreated, UserDocument> mapCreatedToDocument = e -> {
    UserDocument document = new UserDocument();
    document.setFirstName(e.firstName());
    document.setLastName(e.lastName());
    ContactInfoDocument contactInfoDocument = new ContactInfoDocument();
    e.email().ifPresent(contactInfoDocument::setEmail);
    e.phoneNumber().ifPresent(contactInfoDocument::setPhoneNumber);
    document.setContactInformationDocument(contactInfoDocument);
    document.setCreatedAt(e.eventCreatedAt());
    return document;
  };

  Function<UserCreated, UserPayload> mapCreatedToPayload = (e) -> {
    ContactInfoPayload infoPayload = ContactInfoPayload.builder()
        .userId(e.id())
        .email(e.email())
        .phoneNumber(e.phoneNumber())
        .build();
    return UserPayload.builder()
        .id(e.id())
        .version(e.version())
        .createdAt(e.eventCreatedAt())
        .firstName(e.firstName())
        .lastName(e.lastName())
        .contactInfo(infoPayload)
        .version(e.version())
        .build();
  };

  Function<ContactInfoCreated, ContactInfoPayload> mapContactCreatedToPayload = e ->
      ContactInfoPayload.builder()
          .userId(e.userId())
          .phoneNumber(e.phoneNumber())
          .email(e.email())
          .build();

  Function<ContactInfoCreated, ContactInfoDocument> mapContInfoCreatedToDoc = created -> {
    ContactInfoDocument contactInfoDocument = new ContactInfoDocument();
    contactInfoDocument.setCreatedAt(created.createdAt());
    created.email().ifPresent(contactInfoDocument::setEmail);
    created.phoneNumber().ifPresent(contactInfoDocument::setPhoneNumber);
    return contactInfoDocument;
  };

}
