package com.mz.user;

import com.mz.user.model.ContactInfoDocument;
import com.mz.user.model.UserDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Objects;
import reactor.test.StepVerifier.Step;

@ExtendWith(SpringExtension.class)
@DataMongoTest
class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  @Test
  void save() {
    UserDocument userDocument = new UserDocument();
    userDocument.setLastName("LastName");
    userDocument.setFirstName("FirstName");
    userDocument.setCreatedAt(Instant.now());

    ContactInfoDocument contactInfoDocument = new ContactInfoDocument();
    contactInfoDocument.setPhoneNumber("+419888999");
    contactInfoDocument.setEmail("test@test.com");
    contactInfoDocument.setCreatedAt(Instant.now());

    userDocument.setContactInformationDocument(contactInfoDocument);

    Mono<UserDocument> result = userRepository.save(userDocument);

    StepVerifier.create(result)
        .expectNextMatches(nextValue ->
            nextValue.getLastName().equals("LastName")
                && nextValue.getFirstName().equals("FirstName")
                && Objects.nonNull(nextValue.getId())
                && Objects.nonNull(nextValue.getVersion().get())
                && Objects.nonNull(nextValue.getContactInformationDocument()))
        .expectComplete().verify();

    Mono<UserDocument> resultUpdate = userRepository.findAll().next().flatMap(d -> {
      d.setFirstName("test2");
      return userRepository.save(d);
    });

    StepVerifier.create(resultUpdate)
        .expectNextMatches(nextVal -> nextVal.getVersion().get().equals(1L));
  }
}