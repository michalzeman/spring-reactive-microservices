package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.shortener.ShortenerService;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Created by zemi on 29/05/2018.
 */
@Service
public class ShortenerApplicationServiceImpl implements ShortenerService {

  private static final Log log = LogFactory.getLog(ShortenerApplicationServiceImpl.class);

  private final AggregateService<ShortenerDto> aggregateService;

  public ShortenerApplicationServiceImpl(AggregateService<ShortenerDto> aggregateService) {
    this.aggregateService = aggregateService;
  }

  @Override
  public Mono<ShortenerDto> create(CreateShortener createShortener) {
    log.debug("execute() ->");
    return aggregateService.execute(UUID.randomUUID().toString(), createShortener);
  }

  @Override
  public Mono<ShortenerDto> update(UpdateShortener shortener) {
    log.debug("update() ->");
    return aggregateService.execute(shortener.id(), shortener);
  }

}
