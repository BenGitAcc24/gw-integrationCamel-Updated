package com.gw_camel_integration.processor;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class XmlDeclarationStripper implements Processor {

    @Override
    public void process(Exchange exchange) {
        String body = exchange.getIn().getBody(String.class);
        if (body != null) {
            // Remove XML declaration if present at the start of the payload
            body = body.replaceFirst("^\\s*<\\?xml[^>]+\\?>", "");
            exchange.getIn().setBody(body);
        }
    }
}
