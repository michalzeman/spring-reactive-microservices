package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.aggregates.AbstractRootAggregate;
import com.mz.reactivedemo.common.aggregates.Id;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.domain.events.ContactInfoCreated;
import com.mz.user.domain.events.UserCreated;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.commands.CreateContactInfo;
import com.mz.user.messages.commands.CreateUser;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by zemi on 02/01/2019.
 */
public class UserAggregate extends AbstractRootAggregate<UserState> {

  private Optional<Id> id = Optional.empty();

  private Optional<ContactInfo> contactInformation = Optional.empty();

  private FirstName firstName;

  private LastName lastName;

  private Optional<Long> version = Optional.empty();

  private Instant createdAt;

  private UserAggregate() {
  }

  private UserAggregate(UserDto userDto) {
    id = Optional.of(new Id(userDto.id()));
    firstName = new FirstName(userDto.firstName());
    lastName = new LastName(userDto.lastName());
    version = Optional.of(userDto.version());
    createdAt = userDto.createdAt();

    contactInformation = userDto.contactInformation()
        .map(c ->
            ContactInfo.builder()
                .userId(id)
                .email(c.email().map(Email::new))
                .phoneNumber(c.phoneNumber().map(PhoneNumber::new))
                .createdAt(c.createdAt())
                .build());
  }

  private Optional<Event> createUser(CreateUser cmd) {
    this.firstName = new FirstName(cmd.firstName());
    this.lastName = new LastName(cmd.lastName());
    this.createdAt = Instant.now();
    this.contactInformation = cmd.contactInformation()
        .map(c -> ContactInfo.builder()
            .email(c.email().map(Email::new))
            .phoneNumber(c.phoneNumber().map(PhoneNumber::new))
            .build());
    return Optional.of(UserCreated.builder()
        .firstName(firstName.value)
        .lastName(lastName.value)
        .version(version)
        .email(this.contactInformation.flatMap(ci -> ci.email().map(em -> em.value)))
        .phoneNumber(this.contactInformation.flatMap(ci -> ci.phoneNumber().map(n -> n.value)))
        .build());
  }

  private Optional<ContactInfoCreated> createContactInformation(CreateContactInfo cmd) {
    this.contactInformation = Optional.ofNullable(cmd)
        .map(c -> ContactInfo.builder()
            .email(c.email().map(Email::new))
            .phoneNumber(c.phoneNumber().map(PhoneNumber::new))
            .build());
    return this.contactInformation
        .map(c -> ContactInfoCreated.builder()
            .userId(id.get().value)
            .createdAt(Instant.now())
            .email(c.email().map(e -> e.value))
            .phoneNumber(c.phoneNumber().map(p -> p.value))
            .userVersion(version)
            .build());
  }

  public UserDto toDto() {
    return UserDto.builder()
//        .eventId(eventId.map(i -> i.value))
        .firstName(this.firstName.value)
        .lastName(this.lastName.value)
        .createdAt(this.createdAt)
//        .version(this.version)
        .contactInformation(contactInformation.map(ContactInfo::toDto))
        .build();
  }

  public static UserAggregate of(UserDto userDto) {
    return new UserAggregate(userDto);
  }

  public static UserAggregate of() {
    return new UserAggregate();
  }

  @Override
  protected Optional<Event> behavior(Command cmd) {
    if (cmd instanceof CreateUser) {
      return createUser((CreateUser) cmd);
    } else if (cmd instanceof CreateContactInfo) {
      return createContactInformation((CreateContactInfo) cmd).map(e -> e);
    }
    return Optional.empty();
  }

  @Override
  protected UserState toResult() {
    return UserState.builder()
        .firstName(this.firstName.value)
        .lastName(this.lastName.value)
        .id(this.id.map(i -> i.value))
        .version(this.version)
        .createdAt(this.createdAt)
        .contactInformation(this.contactInformation.map(c ->
          UserState.ContactInfoState.builder()
              .email(c.email().map(e -> e.value))
              .phoneNumber(c.phoneNumber().map(p -> p.value))
              .createdAt(this.createdAt)
              .build()
        ))
        .build();
  }
}