package com.mz.user.domain.aggregate;

import com.mz.reactivedemo.common.aggregates.AbstractRootAggregate;
import com.mz.reactivedemo.common.aggregates.Id;
import com.mz.reactivedemo.common.api.events.Command;
import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.domain.events.ContactInfoCreated;
import com.mz.user.domain.events.UserCreated;
import com.mz.user.dto.UserDto;
import com.mz.user.messages.CreateContactInfo;
import com.mz.user.messages.CreateUser;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by zemi on 02/01/2019.
 */
public class UserRootEntity extends AbstractRootAggregate<UserDto> {

  private Optional<Id> id = Optional.empty();

  private Optional<ContactInfo> contactInformation = Optional.empty();

  private FirstName firstName;

  private LastName lastName;

  private Optional<Long> version = Optional.empty();

  private Instant createdAt;

  private UserRootEntity() {
  }

  private UserRootEntity(UserDto userDto) {
    id = Optional.of(new Id(userDto.id().get()));
    userDto.firstName().ifPresent(f -> firstName = new FirstName(f));
    userDto.lastName().ifPresent(l -> lastName = new LastName(l));
    version = userDto.version();
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

  private ImmutableSet<Event> createUser(CreateUser cmd) {
    this.firstName = new FirstName(cmd.firstName());
    this.lastName = new LastName(cmd.lastName());
    this.createdAt = Instant.now();
    ImmutableSet<Event> events = Sets.immutable.of(UserCreated.builder().user(toDto()).build());
    return events.union(cmd.contactInformation().map(c -> createContactInformation(c)).orElse(Sets.immutable.empty()));
  }

  private ImmutableSet<Event> createContactInformation(CreateContactInfo cmd) {
    this.contactInformation = Optional.ofNullable(cmd)
        .map(c -> ContactInfo.builder()
            .email(c.email().map(Email::new))
            .phoneNumber(c.phoneNumber().map(PhoneNumber::new))
            .build());
    return this.contactInformation
        .map(c -> Sets.immutable.<Event>of(ContactInfoCreated.builder()
            .contactInformation(c.toDto())
            .build())).orElse(Sets.immutable.empty());
  }

  public UserDto toDto() {
    return UserDto.builder()
        .id(id.map(i -> i.value))
        .firstName(this.firstName.value)
        .lastName(this.lastName.value)
        .createdAt(this.createdAt)
        .version(this.version)
        .contactInformation(contactInformation.map(ContactInfo::toDto))
        .build();
  }

  public static UserRootEntity of(UserDto userDto) {
    return new UserRootEntity(userDto);
  }

  public static UserRootEntity of() {
    return new UserRootEntity();
  }

  @Override
  protected ImmutableSet<Event> behavior(Command cmd) {
    if (cmd instanceof CreateUser) {
      return createUser((CreateUser) cmd);
    } else if (cmd instanceof CreateContactInfo) {
      return createContactInformation(((CreateContactInfo) cmd));
    }
    return Sets.immutable.empty();
  }

  @Override
  protected UserDto toResult() {
    return toDto();
  }
}
