package com.mz.user;

import com.mz.reactivedemo.adapter.persistance.persistence.PersistenceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PersistenceConfiguration.class)
public class UserApplicationConfiguration {

}
