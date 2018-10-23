package com.mz.reactivedemo.shortener.api.commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mz.reactivedemo.common.events.Command;
import org.immutables.value.Value;

/**
 * Created by zemi on 29/05/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableCreateShortener.class)
@JsonDeserialize(as = ImmutableCreateShortener.class)
public interface CreateShortener extends Command {
  String url();

}
