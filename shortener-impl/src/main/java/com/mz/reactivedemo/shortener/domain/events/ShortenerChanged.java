package com.mz.reactivedemo.shortener.domain.events;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.events.ShortenerEvent;

/**
 * Created by zemi on 21/10/2018.
 */
public interface ShortenerChanged extends ShortenerEvent {
  ShortenerDto shortener();
}
