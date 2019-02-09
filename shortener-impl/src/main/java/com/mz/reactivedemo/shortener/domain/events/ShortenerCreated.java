package com.mz.reactivedemo.shortener.domain.events;

import org.immutables.value.Value;

/**
 * Created by zemi on 29/05/2018.
 */
@Value.Immutable
public interface ShortenerCreated extends ShortenerChanged {
}
