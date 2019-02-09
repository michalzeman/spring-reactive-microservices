package com.mz.user.dto;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by zemi on 16/01/2019.
 */
public interface BasicDto {

  Optional<String> id();

  Instant createdAt();

  Optional<Long> version();

}
