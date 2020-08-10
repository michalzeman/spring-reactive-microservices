package com.mz.user.dto;

import java.time.Instant;

/**
 * Created by zemi on 16/01/2019.
 */
public interface BasicDto {

  String id();

  Instant createdAt();

  Long version();

}
