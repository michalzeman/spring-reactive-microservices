package com.mz.reactivedemo.shortener.impl;

import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.reactivedemo.adapter.persistance.document.DocumentReadOnlyRepository;
import com.mz.reactivedemo.shortener.ShortenerService;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.user.dto.UserDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Created by zemi on 29/05/2018.
 */
@Service
public class ShortenerApplicationServiceImpl implements ShortenerService {

  private static final Log log = LogFactory.getLog(ShortenerApplicationServiceImpl.class);

  private final AggregateService<ShortenerDto> aggregateService;

  private final DocumentReadOnlyRepository<String, UserDto> userReadOnlyRepository;

  public ShortenerApplicationServiceImpl(
      AggregateService<ShortenerDto> aggregateService,
      DocumentReadOnlyRepository<String, UserDto> userReadOnlyRepository
  ) {
    this.aggregateService = requireNonNull(aggregateService, "aggregateService is required");
    this.userReadOnlyRepository = requireNonNull(userReadOnlyRepository, "userReadOnlyRepository is required");
  }

  @Override
  public Mono<ShortenerDto> create(CreateShortener createShortener) {
    log.debug("execute() ->");
    return userReadOnlyRepository.get(createShortener.userId())
        .switchIfEmpty(Mono.error(new RuntimeException(String.format("User with id %s doesn't exist", createShortener.userId()))))
        .flatMap(user -> aggregateService.execute(UUID.randomUUID().toString(), createShortener));
  }

  @Override
  public Mono<ShortenerDto> update(UpdateShortener shortener) {
    log.debug("update() ->");
    return aggregateService.execute(shortener.id(), shortener);
  }

}
