package com.mz.user;

import com.mz.user.domain.event.ContactInfoCreated;
import com.mz.user.domain.event.UserCreated;
import com.mz.user.dto.ContactInfoDto;
import com.mz.user.dto.UserDto;
import com.mz.user.message.ContactInfoPayload;
import com.mz.user.message.UserPayload;
import com.mz.user.view.ContactInfoDocument;
import com.mz.user.view.UserDocument;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Optional;
import java.util.function.Function;

public interface UserMapper {

  Function<UserDocument, UserDto> mapToDto = doc ->
      UserDto.builder()
          .id(doc.getId())
          .firstName(doc.getFirstName())
          .lastName(doc.getLastName())
          .createdAt(doc.getCreatedAt())
          .version(doc.getVersion())
          .shortenerIds(Lists.immutable.ofAll(doc.getShortenerIds()))
          .contactInformation(Optional.ofNullable(doc.getContactInformationDocument())
              .map(c -> ContactInfoDto.builder()
                  .userId(doc.getId())
                  .createdAt(c.getCreatedAt())
                  .email(Optional.ofNullable(c.getEmail()))
                  .phoneNumber(Optional.ofNullable(c.getPhoneNumber()))
                  .build()))
          .build();

  Function<UserDto, UserDocument> mapToDocument = dto -> {
    UserDocument userDocument = new UserDocument(dto.id(), dto.firstName(),
        dto.lastName(), dto.version(), dto.createdAt(),
        dto.contactInformation().map(UserMapper.mapToContactInfoDocument).orElse(null));
    userDocument.setShortenerIds(dto.shortenerIds());
    return userDocument;
  };

  Function<ContactInfoDto, ContactInfoDocument> mapToContactInfoDocument = dto ->
      new ContactInfoDocument(dto.email().orElse(null),
          dto.phoneNumber().orElse(null), dto.createdAt());

  Function<UserCreated, UserPayload> mapCreatedToPayload = (e) -> {
    ContactInfoPayload infoPayload = ContactInfoPayload.builder()
        .userId(e.aggregateId())
        .email(e.email())
        .phoneNumber(e.phoneNumber())
        .build();
    return UserPayload.builder()
        .id(e.aggregateId())
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
          .userId(e.aggregateId())
          .phoneNumber(e.phoneNumber())
          .email(e.email())
          .build();

}
