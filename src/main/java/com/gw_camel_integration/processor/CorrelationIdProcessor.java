package com.gw_camel_integration.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.MDC;

import java.util.UUID;

public class CorrelationIdProcessor implements Processor {
  @Override
  public void process(Exchange exchange) {
    String correlationId = UUID.randomUUID().toString();
    exchange.setProperty("correlationId", correlationId);
    MDC.put("correlationId", correlationId);
  }
}
