package com.mz.statistic;

import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by zemi on 29/05/2018.
 */
public interface StatisticService {
  Flux<StatisticDocument> getAll();

  Mono<Long> numberOfViews(String postId);


  Mono<Long> eventsCount(EventType type);


}
