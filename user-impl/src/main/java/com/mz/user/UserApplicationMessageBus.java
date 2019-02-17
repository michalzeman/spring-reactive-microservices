package com.mz.user;

import com.mz.reactivedemo.common.api.events.Event;
import com.mz.user.dto.UserDto;
import reactor.core.publisher.Flux;

public interface UserApplicationMessageBus {

    void publishEvent(Event event);

    void publishDocumentMessage(UserDto dto);

    Flux<Event> events();

    Flux<UserDto> documents();
}
