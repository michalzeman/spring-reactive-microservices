package com.mz.reactivedemo.shortener.domain.events;

import org.immutables.value.Value;

/**
 * Created by zemi on 30/09/2018.
 */
@Value.Immutable
public interface ShortenerUpdated extends ShortenerChanged {
}
