package com.github.rkbalgi.apps.microsrvc.scaffold.handlers;

import java.time.LocalDateTime;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 *
 *
 */
public class GreetingHandlerFunction implements HandlerFunction {

  private final DefaultDataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

  @Override
  public Mono<ServerResponse> handle(ServerRequest serverRequest) {

    String jsonGreeting = "{\n"
        + "  \"greeting\": \"hello world @ " + LocalDateTime.now() + "\"\n"
        + "}";

    DataBuffer buf = dataBufferFactory.wrap(jsonGreeting.getBytes());

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromDataBuffers(Mono.just(buf)));
  }
}
