package com.mz.user.domain.events;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.dto.ContactInfoDto;
import org.immutables.value.Value;

/**
 * Created by zemi on 16/01/2019.
 */
@Value.Immutable
public interface ContactInfoCreated extends Event {

  ContactInfoDto contactInformation();

  static ImmutableContactInfoCreated.Builder builder() {
    return ImmutableContactInfoCreated.builder();
  }

}
