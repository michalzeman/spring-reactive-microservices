package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.shortener.view.ShortenerDocument;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Created by zemi on 29/05/2018.
 */
@Repository
public interface ShortenerRepository extends ReactiveCrudRepository<ShortenerDocument, String> {
  Mono<ShortenerDocument> findByKey(String key);
}
