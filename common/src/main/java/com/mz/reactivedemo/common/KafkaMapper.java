package com.mz.reactivedemo.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.kafka.sender.SenderRecord;

import java.util.function.Function;

public enum KafkaMapper {

  FN;

  public <T> Function<String, T> mapFromJson(ObjectMapper mapper, Class<T> clazz) {
    return json -> {
      try {
        return mapper.readValue(json, clazz);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public <T> Function<T, SenderRecord<String, String, T>> mapToRecord(
      String topic,
      ObjectMapper mapper,
      Function<T, String> idMapper
  ) {
    return value -> {
      try {
        final var producerRecord = new ProducerRecord<>(
            topic, idMapper.apply(value),
            mapper.writeValueAsString(value)
        );
        return SenderRecord.create(producerRecord, value);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    };
  }
}
