package com.mz.user.view.impl;

import com.mz.user.dto.UserDto;
import com.mz.user.view.UserQuery;
import com.mz.user.view.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mz.user.UserMapper.FN;

@Service
public class UserQueryImpl implements UserQuery {

  private final UserRepository repository;

  public UserQueryImpl(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public Mono<UserDto> getById(String id) {
    return repository.findById(id).map(FN.mapToDto);
  }

  @Override
  public Flux<UserDto> getAll() {
    return repository.findAll().map(FN.mapToDto);
  }
}
