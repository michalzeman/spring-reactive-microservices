package com.mz.user.impl;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.UserApplicationMessageBus;
import com.mz.user.dto.UserDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Service
public class UserApplicationMessageBusImpl implements UserApplicationMessageBus {

    protected final ReplayProcessor<Event> events = ReplayProcessor.create(1);

    protected final FluxSink<Event> eventSink = events.sink();

    protected final ReplayProcessor<UserDto> documents = ReplayProcessor.create(1);

    protected final FluxSink<UserDto> documentsSink = documents.sink();

    @Override
    public void publishEvent(Event event) {
        Optional.ofNullable(event).ifPresent(e -> eventSink.next(e));
    }

    @Override
    public void publishDocumentMessage(UserDto dto) {
        Optional.ofNullable(dto).ifPresent(d -> documentsSink.next(d));
    }

    @Override
    public Flux<Event> events() {
        return events.publishOn(Schedulers.parallel());
    }

    @Override
    public Flux<UserDto> documents() {
        return documents.publishOn(Schedulers.parallel());
    }

}
