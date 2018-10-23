package com.mz.reactivedemo.shortener.events;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDTO;
import com.mz.reactivedemo.shortener.api.events.ShortenerEvent;

/**
 * Created by zemi on 21/10/2018.
 */
public interface ShortenerChanged extends ShortenerEvent {
  ShortenerDTO shortener();
}
