package com.mz.user;

import com.mz.user.model.UserDocument;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<UserDocument, String> {
}
