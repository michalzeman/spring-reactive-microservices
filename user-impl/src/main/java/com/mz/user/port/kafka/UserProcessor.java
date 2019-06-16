package com.mz.user.port.kafka;


import com.mz.reactivedemo.common.api.events.Event;
import com.mz.reactivedemo.common.api.util.CaseMatch;
import com.mz.reactivedemo.common.utils.Logger;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.dto.UserDto;
import com.mz.user.message.event.UserChangedEvent;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@EnableBinding(UserBinding.class)
@Service
public class UserProcessor {

  private Logger logger = new Logger(LogFactory.getLog(UserProcessor.class));

  private final UserBinding userBinding;

  private final UserApplicationMessageBus messageBus;

  public UserProcessor(UserBinding userBinding, UserApplicationMessageBus messageBus) {
    this.userBinding = userBinding;
    this.messageBus = messageBus;
  }

  @PostConstruct
  void onInit() {
    logger.debug(() -> "UserProcessor.onInit() ->");
    messageBus.events()
        .subscribeOn(Schedulers.parallel())
        .subscribe(this::processEvent, this::processError);
    messageBus.documents()
        .subscribeOn(Schedulers.parallel())
        .subscribe(this::processDocument, this::processError);
  }

  private void processDocument(UserDto doc) {
    userBinding.userDocumentOut()
        .send(MessageBuilder
            .withPayload(doc)
            .setHeader(KafkaHeaders.MESSAGE_KEY, doc.id().getBytes())
            .build());
  }

  private void processError(Throwable throwable) {
    logger.log().error(throwable);
  }

  private void processEvent(Event event) {
    CaseMatch.match(event)
        .when(UserChangedEvent.class, e -> {
          MessageChannel messageChannel = userBinding.userChangedOut();
          messageChannel.send(MessageBuilder
              .withPayload(e)
              .setHeader(KafkaHeaders.MESSAGE_KEY, e.aggregateId().getBytes())
              .build());
        });
  }
}
