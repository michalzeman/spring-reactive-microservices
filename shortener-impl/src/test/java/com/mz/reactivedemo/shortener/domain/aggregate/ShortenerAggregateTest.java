package com.mz.reactivedemo.shortener.domain.aggregate;

import com.mz.reactivedemo.common.ValidateResult;
import com.mz.reactivedemo.common.api.util.Try;
import com.mz.reactivedemo.shortener.api.command.CreateShortener;
import com.mz.reactivedemo.shortener.api.command.UpdateShortener;
import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.domain.event.ShortenerCreated;
import com.mz.reactivedemo.shortener.domain.event.ShortenerUpdated;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class ShortenerAggregateTest {

  @Test
  void create() {
    String id = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();
    ShortenerAggregate shortenerAggregate = ShortenerAggregate.of(id);
    CreateShortener createShortener = CreateShortener.builder()
        .url("www.test.url")
        .userId(userId)
        .build();
    Try<ValidateResult> resultTry = shortenerAggregate.validate(createShortener);
    Assertions.assertTrue(resultTry.get().events().size() == 1);
    Assertions.assertTrue(resultTry.get().events().stream().allMatch(e -> e instanceof ShortenerCreated));

    resultTry.get().events().forEach(e -> shortenerAggregate.apply(e));
    ShortenerDto state = shortenerAggregate.state();
    Assertions.assertEquals(state.url(), "www.test.url");
    Assertions.assertEquals(state.id(), id);
    Assertions.assertEquals(state.version().longValue(), 0L);
    Assertions.assertEquals(state.userId().get(), userId);
  }

  @Test
  void update() {
    String id = UUID.randomUUID().toString();
    String userId = UUID.randomUUID().toString();
    ShortenerAggregate shortenerAggregate = ShortenerAggregate.of(id);
    CreateShortener createShortener = CreateShortener.builder()
        .url("www.test.url")
        .userId(userId)
        .build();
    Try<ValidateResult> resultTry = shortenerAggregate.validate(createShortener);
    resultTry.get().events().forEach(e -> shortenerAggregate.apply(e));
    ShortenerDto state = shortenerAggregate.state();

    Assertions.assertEquals(state.url(), "www.test.url");
    Assertions.assertEquals(state.id(), id);
    Assertions.assertEquals(state.version().longValue(), 0L);

    Try<ValidateResult> validateUpdate = shortenerAggregate.validate(UpdateShortener.builder()
        .id(id)
        .url("www.update.rl")
        .build());
    validateUpdate.get().events().forEach(e -> shortenerAggregate.apply(e));
    Assertions.assertTrue(validateUpdate.get().events().stream().allMatch(e -> e instanceof ShortenerUpdated));

    ShortenerDto updatedState = shortenerAggregate.state();
    Assertions.assertEquals(updatedState.url(), "www.update.rl");
    Assertions.assertEquals(updatedState.id(), id);
    Assertions.assertEquals(updatedState.version().longValue(), 1L);
  }

}
