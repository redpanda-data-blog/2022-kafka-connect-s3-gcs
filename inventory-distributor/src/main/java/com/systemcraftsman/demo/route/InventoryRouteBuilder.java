package com.systemcraftsman.demo.route;

import org.apache.camel.builder.RouteBuilder;

public class InventoryRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("file:...")
            .to("kafka:london-inventory");
    }
}

