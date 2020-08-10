package com.mz.statistic.adapters.shortener;

import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerViewed;
import reactor.core.publisher.Flux;

/**
 * Created by zemi on 07/10/2018.
 */
public interface ShortenerSubscriber {

  Flux<ShortenerViewed> eventsShortenerViewed();

  Flux<ShortenerChangedEvent> shortenerChanged();

}
