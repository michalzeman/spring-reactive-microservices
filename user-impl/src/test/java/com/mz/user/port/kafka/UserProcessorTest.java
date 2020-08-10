package com.mz.user.port.kafka;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.message.UserPayload;
import com.mz.user.message.event.UserChangedEvent;
import com.mz.user.message.event.UserEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProcessorTest {

  @Mock
  UserBinding userBinding;

  @Mock
  UserApplicationMessageBus messageBus;

  @InjectMocks
  UserProcessor userProcessor;

  @Test
  void processUserChangedEvent() {
    MessageChannel messageChangedChannel = Mockito.mock(MessageChannel.class);
    String id = UUID.randomUUID().toString();
    Flux<Event> events = Flux.just(UserChangedEvent.builder()
        .aggregateId(id)
        .payload(UserPayload.builder()
            .firstName("FirstNameTest")
            .lastName("LastNameTest")
            .version(0L)
            .createdAt(Instant.now())
            .id(id)
            .build())
        .type(UserEventType.USER_CREATED)
        .build());
    when(messageBus.events()).thenReturn(events);
    when(messageBus.documents()).thenReturn(Flux.empty());
    when(userBinding.userChangedOut()).thenReturn(messageChangedChannel);

    userProcessor.onInit();
    verify(messageChangedChannel,  Mockito.atMost(1)).send(any(Message.class));
  }

}
