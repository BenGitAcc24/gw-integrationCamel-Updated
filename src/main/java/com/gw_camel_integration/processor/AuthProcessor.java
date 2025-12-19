package com.gw_camel_integration.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthProcessor implements Processor {

  private final String expectedUser = System.getenv().getOrDefault("GW_USER", "gwuser");
  private final String expectedPass = System.getenv().getOrDefault("GW_PASS", "gwpass");

  @Override
  public void process(Exchange exchange) {
    String auth = exchange.getIn().getHeader("Authorization", String.class);
    if (auth == null || !auth.startsWith("Basic ")) {
      exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
      exchange.getMessage().setBody("{\"error\":\"Unauthorized\"}");
      exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
      return;
    }
    String base64 = auth.substring("Basic ".length());
    String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
    String[] parts = decoded.split(":", 2);
    if (parts.length != 2 || !parts[0].equals(expectedUser) || !parts[1].equals(expectedPass)) {
      exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 403);
      exchange.getMessage().setBody("{\"error\":\"Forbidden\"}");
      exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
    }
  }
}
