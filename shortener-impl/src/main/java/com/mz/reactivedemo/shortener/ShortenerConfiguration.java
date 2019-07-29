package com.mz.reactivedemo.shortener;

import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregatePersistenceConfiguration;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.aggregate.ShortenerAggregate;
import com.mz.reactivedemo.shortener.impl.ShortenerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AggregatePersistenceConfiguration.class)
public class ShortenerConfiguration {

  @Bean
  public AggregateService<ShortenerDto> aggregateService(
      ShortenerFunctions.UpdateView updateView,
      ShortenerFunctions.PublishChangedEvent publishChanged,
      ShortenerFunctions.PublishDocumentMessage publishDocumentMessage,
      AggregateRepository aggregateRepository) {
    return AggregateService.of(aggregateRepository,
        AggregateFactory.build(ShortenerAggregate::of, ShortenerAggregate::of),
        updateView, publishChanged,
        publishDocumentMessage);
  }

}
