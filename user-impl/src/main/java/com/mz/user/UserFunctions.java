package com.mz.user;

import com.mz.user.domain.events.ContactInfoCreated;
import com.mz.user.domain.events.UserCreated;
import com.mz.user.dto.ContactInfoDto;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.ContactInfoPayload;
import com.mz.user.messages.UserPayload;
import com.mz.user.model.ContactInfoDocument;
import com.mz.user.model.UserDocument;

import java.util.Optional;
import java.util.function.BiFunction;
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
    e.firstName().ifPresent(document::setFirstName);
    e.lastName().ifPresent(document::setLastName);
    e.contactInfoCreated().ifPresent(c ->
        document.setContactInformationDocument(UserFunctions.mapContactInfoPayloadToDoc.apply(c))
    );
    document.setCreatedAt(e.eventCreatedAt());
    return document;
  };

  BiFunction<UserCreated, UserDto, UserPayload> mapCreatedToPayload = (e, dto) ->
      UserPayload.builder()
          .id(dto.id())
          .version(dto.version())
          .createdAt(dto.createdAt())
          .firstName(e.firstName())
          .lastName(e.lastName())
          .contactInfo(e.contactInfoCreated())
          .build();

  Function<ContactInfoCreated, ContactInfoPayload> mapContactCreatedToPlayload = e ->
      ContactInfoPayload.builder()
          .userId(e.userId())
          .phoneNumber(e.phoneNumber())
          .email(e.email())
          .build();


  Function<ContactInfoPayload, ContactInfoDocument> mapContactInfoPayloadToDoc = payload -> {
    ContactInfoDocument contactInfoDocument = new ContactInfoDocument();
//    contactInfoDocument.setCreatedAt();
    payload.email().ifPresent(contactInfoDocument::setEmail);
    payload.phoneNumber().ifPresent(contactInfoDocument::setPhoneNumber);
    return contactInfoDocument;
  };

  Function<ContactInfoCreated, ContactInfoDocument> mapContInfoCreatedToDoc = created -> {
    ContactInfoDocument contactInfoDocument = new ContactInfoDocument();
    contactInfoDocument.setCreatedAt(created.createdAt());
    created.email().ifPresent(contactInfoDocument::setEmail);
    created.phoneNumber().ifPresent(contactInfoDocument::setPhoneNumber);
    return contactInfoDocument;
  };

}
