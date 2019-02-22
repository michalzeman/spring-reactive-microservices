package com.mz.user.messages.commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mz.reactivedemo.common.api.events.Command;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * Created by zemi on 13/01/2019.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCreateContactInfo.class)
@JsonDeserialize(as = ImmutableCreateContactInfo.class)
public interface CreateContactInfo extends Command {

  Optional<String> email();

  Optional<String> phoneNumber();

  static ImmutableCreateContactInfo.Builder builder() { return ImmutableCreateContactInfo.builder(); }
}
