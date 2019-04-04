package com.mz.statistic.adapters.shortener;

import com.mz.reactivedemo.shortener.api.dto.ShortenerDto;
import com.mz.reactivedemo.shortener.api.event.ShortenerChangedEvent;
import com.mz.reactivedemo.shortener.api.event.ShortenerViewed;
import com.mz.reactivedemo.shortener.api.topics.ShortenerTopics;
import com.mz.statistic.ShortenerSubscriber;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

/**
 * Created by zemi on 14/10/2018.
 */
@Component
@EnableBinding(ShortenerSink.class)
public class ShortenerSubscriberImpl implements ShortenerSubscriber {

  private final ReplayProcessor<ShortenerViewed> events = ReplayProcessor.create(1);

  private final FluxSink<ShortenerViewed> eventSink = events.sink();

  private final ReplayProcessor<ShortenerChangedEvent> changedEvents = ReplayProcessor.create(1);

  private final FluxSink<ShortenerChangedEvent> changedEventsSink = changedEvents.sink();

  @StreamListener
  public void process(@Input(ShortenerTopics.SHORTENER_VIEWED) KStream<String, ShortenerViewed> shortenerViewed,
                      @Input(ShortenerTopics.SHORTENER_DOCUMENT) KStream<String, ShortenerDto> shortenerDoc,
                      @Input(ShortenerTopics.SHORTENER_CHANGED) KStream<String, ShortenerChangedEvent> shortenerChanged) {
    shortenerViewed.foreach((k, v) -> eventSink.next(v));
    shortenerDoc.foreach((k, v) -> System.out.println(v));
    shortenerChanged.foreach((k, v) -> changedEventsSink.next(v));
  }

  @Override
  public Flux<ShortenerViewed> eventsShortenerViewed() {
    return events.publishOn(Schedulers.parallel());
  }

  @Override
  public Flux<ShortenerChangedEvent> shortenerChanged() {
    return changedEvents.publishOn(Schedulers.parallel());
  }
}
