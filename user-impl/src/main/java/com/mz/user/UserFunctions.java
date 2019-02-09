package com.mz.user;

import com.mz.user.dto.ContactInfoDto;
import com.mz.user.dto.UserDto;
import com.mz.user.model.ContactInfoDocument;
import com.mz.user.model.UserDocument;

import java.util.Optional;
import java.util.function.Function;

public interface UserFunctions {

  Function<UserDocument, UserDto> mapToDto = doc ->
      UserDto.builder()
          .id(doc.getId())
          .firstName(Optional.ofNullable(doc.getFirstName()))
          .lastName(Optional.ofNullable(doc.getLastName()))
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
      new UserDocument(dto.id().orElse(null), dto.firstName().orElse(null),
          dto.lastName().orElse(null), dto.version().orElse(null), dto.createdAt(),
          dto.contactInformation().map(UserFunctions.mapToContactInfoDocument).orElse(null));

  Function<ContactInfoDto, ContactInfoDocument> mapToContactInfoDocument = dto ->
      new ContactInfoDocument(dto.email().orElse(null),
          dto.phoneNumber().orElse(null), dto.createdAt());

}
