package com.mz.user;

import com.mz.reactivedemo.adapter.persistance.AggregateFactory;
import com.mz.reactivedemo.adapter.persistance.AggregatePersistenceConfiguration;
import com.mz.reactivedemo.adapter.persistance.AggregateRepository;
import com.mz.reactivedemo.adapter.persistance.AggregateService;
import com.mz.user.domain.aggregate.UserAggregate;
import com.mz.user.dto.UserDto;
import com.mz.user.impl.UserFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AggregatePersistenceConfiguration.class)
public class UserApplicationConfiguration {

    @Bean
    public AggregateService<UserDto> aggregateService(
        UserFunctions.UpdateUserView updateUserView,
        UserFunctions.PublishUserChangedEvent publishUserChanged,
        UserFunctions.PublishUserDocumentMessage publishDocumentMessage,
        AggregateRepository aggregateRepository) {
        return AggregateService.of(aggregateRepository,
            AggregateFactory.build(UserAggregate::of, UserAggregate::of),
            updateUserView, publishUserChanged,
            publishDocumentMessage);
    }

}
