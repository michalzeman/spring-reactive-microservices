package com.mz.statistic;

import com.mz.statistic.model.EventType;
import com.mz.statistic.model.StatisticDocument;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Created by zemi on 29/05/2018.
 */
@Repository
public interface StatisticRepository extends ReactiveCrudRepository<StatisticDocument, String> {

  Flux<StatisticDocument> findByUrlAndEventType(String url, EventType eventType);

  Flux<StatisticDocument> findByEventType(EventType eventType);

  Flux<StatisticDocument> findByEventId(String eventId);

}
