package com.mz.reactivedemo.shortener.adapter.user;

import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import static java.util.Objects.requireNonNull;

@Component
public class UserAdapterImpl {

  private final ReceiverOptions<String, String> userChangedReceiverOptions;

  public UserAdapterImpl(ReceiverOptions<String, String> userChangedReceiverOptions) {
    this.userChangedReceiverOptions = requireNonNull(userChangedReceiverOptions, "userChangedReceiverOptions is required");
    init();
  }

  private void init() {
    KafkaReceiver.create(userChangedReceiverOptions).receive()
        .retry()
        .subscribe(record -> System.out.println(record.value()));

  }

}
