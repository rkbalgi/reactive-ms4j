package com.github.rkbalgi.apps.microsrvc.scaffold.handlers;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.primitives.Ints;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 *
 *
 */
public class AddHandlerFunction implements HandlerFunction {


  @Override
  public Mono<ServerResponse> handle(ServerRequest serverRequest) {

    int a = Ints.tryParse(serverRequest.queryParam("a").get());
    int b = Ints.tryParse(serverRequest.queryParam("b").get());

    String httpResponse = new ObjectNode(JsonNodeFactory.instance).put("sum", a + b).toString();

    return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
        .body(fromObject(httpResponse));
  }
}
