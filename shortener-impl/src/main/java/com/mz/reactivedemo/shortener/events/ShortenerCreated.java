package com.mz.reactivedemo.shortener.events;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import com.mz.reactivedemo.shortener.api.events.ShortenerEvent;
import org.immutables.value.Value;

/**
 * Created by zemi on 29/05/2018.
 */
@Value.Immutable
public interface ShortenerCreated extends ShortenerChanged {
}
