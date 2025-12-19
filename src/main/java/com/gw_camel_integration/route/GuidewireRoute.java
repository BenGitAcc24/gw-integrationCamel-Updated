package com.gw_camel_integration.route;


import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.gw_camel_integration.processor.CorrelationIdProcessor;
import com.gw_camel_integration.processor.XmlDeclarationStripper;

@Component
public class GuidewireRoute extends RouteBuilder {

  @Override
  public void configure() {

    // Centralized error mapping
	  onException(ValidationException.class)
      .handled(true)
      .log("XSD validation failed: ${exception.message}, correlation=${exchangeProperty.correlationId}")
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
      .setBody(simple("""
        {"errorCode":"GW-VAL-001","message":"Invalid request payload","correlation":"${exchangeProperty.correlationId}"}"""));

    onException(org.apache.camel.http.base.HttpOperationFailedException.class)
      .handled(true)
      .log("External API error: ${exception.statusCode} ${exception.message}, correlation=${exchangeProperty.correlationId}")
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
      .setBody(simple("""
        {"errorCode":"GW-EXT-002","message":"External service failure","correlation":"${exchangeProperty.correlationId}"}"""));

    onException(Exception.class)
      .handled(true)
      .log("Unhandled exception: ${exception.message}, correlation=${exchangeProperty.correlationId}")
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
      .setBody(simple("""
        {"errorCode":"GW-INT-999","message":"Internal error","correlation":"${exchangeProperty.correlationId}"}"""));

    restConfiguration()
      .component("servlet")
      .bindingMode(RestBindingMode.off)
      .dataFormatProperty("prettyPrint", "true");

    rest("/gw")
      .post("/validate")
      .consumes("application/xml")
      .produces("application/json")
      .to("direct:processGw");

    from("direct:processGw")
      .routeId("gw-validate-route")
      .process(new CorrelationIdProcessor())
      .process(new XmlDeclarationStripper())
      .log("Received Guidewire XML, correlation=${exchangeProperty.correlationId}")
      // XSD validation first
      .to("validator:transform/gw-request.xsd")
      // persist original XML
      .toD("file:data/requests?fileName=${exchangeProperty.correlationId}.xml")
      // transform XML -> JSON via XSLT
      .to("xslt:transform/gw-to-address.xslt")
      .log("Transformed payload: ${body}, correlation=${exchangeProperty.correlationId}")
      .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
      // extract ZIP from JSON and call external API dynamically
      .setHeader("zip").jsonpath("$.zip")
      .toD("https://api.zippopotam.us/us/${header.zip}?bridgeEndpoint=true")
      .log("External API response received, correlation=${exchangeProperty.correlationId}")
      .toD("file:data/responses?fileName=${exchangeProperty.correlationId}.json")
      .setBody(simple("""
        {"status":"ok","correlation":"${exchangeProperty.correlationId}"}"""));
  }
}
