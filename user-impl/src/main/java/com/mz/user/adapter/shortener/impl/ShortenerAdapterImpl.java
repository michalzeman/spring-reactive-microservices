package com.mz.user.adapter.shortener.impl;

import com.mz.reactivedemo.common.utils.Logger;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerEventType;
import com.mz.reactivedemo.shortener.api.topics.ShortenerTopics;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.UserApplicationService;
import com.mz.user.adapter.shortener.ShortenerAdapter;
import com.mz.user.domain.command.AddShortener;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(ShortenerSink.class)
public class ShortenerAdapterImpl implements ShortenerAdapter {

  private Logger logger = new Logger(LogFactory.getLog(ShortenerAdapterImpl.class));

  private final UserApplicationMessageBus messageBus;

  private final UserApplicationService userApplicationService;

  public ShortenerAdapterImpl(UserApplicationMessageBus messageBus, UserApplicationService userApplicationService) {
    this.messageBus = messageBus;
    this.userApplicationService = userApplicationService;
  }

  @StreamListener
  public void process(@Input(ShortenerTopics.SHORTENER_CHANGED) KStream<String, ShortenerChangedEvent> shortenerChanged) {
    shortenerChanged
        .filter((k, v) -> ShortenerEventType.CREATED == v.type())
        .foreach((k, v) -> processShortenerChanged(v));
  }

  private void processShortenerChanged(ShortenerChangedEvent changedEvent) {
      logger.debug(() -> "shortenerChanged ->");
      AddShortener addShortener = AddShortener.builder()
          .shortenerId(changedEvent.payload().id())
          .userId(changedEvent.payload().userId().get())
          .build();
      userApplicationService.execute(addShortener).subscribe();
  }
}
