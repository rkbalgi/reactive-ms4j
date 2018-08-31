package com.github.rkbalgi.apps;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.toHttpHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.ipc.netty.http.server.HttpServer;

/**
 * The main entry point for the spring boot reactor microservices application
 */
public class App {

  private static final Logger LOG = LogManager.getLogger(App.class);

  public static void main(String[] args) throws InterruptedException {

    //read config
    ObjectNode node = null;
    try {
      node = (ObjectNode) new ObjectMapper().readTree(Resources.getResource("app-config.json"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    int httpPort = node.at("/http_config/port").asInt();

    List<RouterFunction> routingFunctions = Lists.newArrayList();

    ArrayNode apiEndpoints = (ArrayNode) node.get("api_endpoints");
    apiEndpoints.forEach(ep -> {
      String httpMethod = ep.at("/http_method").asText();
      String uri = ep.at("/uri").asText();
      HandlerFunction handlerFunction = null;
      try {
        handlerFunction = (HandlerFunction) Class
            .forName(ep.at("/handler_func_impl_class").asText())
            .newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      LOG.info("Adding API endpoint {} with method {}", uri, httpMethod);
      switch (httpMethod) {
        case "GET": {
          routingFunctions.add(route(GET(uri), handlerFunction));
          break;
        }
        default: {
          throw new RuntimeException("Unsupported operation - " + httpMethod);
        }
      }


    });

    Preconditions
        .checkArgument(routingFunctions.size() > 0, "At least one api-endpoint should be supplied");
    LOG.info("No of configured routes - ", routingFunctions.size());

    RouterFunction routingFunction = routingFunctions.get(0);
    routingFunctions.stream().skip(1).forEach(rf -> routingFunction.andOther(rf));

    LOG.info("Starting HTTP instance @ port {}", httpPort);
    HttpServer httpServer = HttpServer.create(httpPort);

    httpServer.startAndAwait(new ReactorHttpHandlerAdapter(toHttpHandler(routingFunction)));


  }
}
