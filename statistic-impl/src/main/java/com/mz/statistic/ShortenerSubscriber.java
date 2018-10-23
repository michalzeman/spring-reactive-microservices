package com.mz.statistic;

import com.mz.reactivedemo.shortener.api.events.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.events.ShortenerViewed;
import reactor.core.publisher.Flux;

/**
 * Created by zemi on 07/10/2018.
 */
public interface ShortenerSubscriber {

  Flux<ShortenerViewed> eventsShortenerViewed();

  Flux<ShortenerChangedEvent> shortenerChanged();

}
