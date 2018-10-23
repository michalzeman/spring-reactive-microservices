package com.mz.reactivedemo.shortener.api.commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mz.reactivedemo.common.events.Command;
import org.immutables.value.Value;

/**
 * Created by zemi on 30/09/2018.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableUpdateShortener.class)
@JsonDeserialize(as = ImmutableUpdateShortener.class)
public interface UpdateShortener extends Command {

  String id();

  String url();
}
