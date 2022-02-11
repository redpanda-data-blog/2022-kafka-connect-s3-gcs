package com.systemcraftsman.demo.route;

import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;

import com.systemcraftsman.demo.model.BookInfo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.dataformat.BindyType;

@ApplicationScoped
public class InventoryRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        from("file://../resources/data?fileName=book-inventory.csv&noop=true&idempotentKey=${file:name}-${file:modified}")
            .transform(body())
            .unmarshal()
            .bindy(BindyType.Csv, BookInfo.class)
            .split(body()).parallelProcessing()
            .setHeader(KafkaConstants.KEY, simple("${body.isbn}"))
            .choice()
                .when(simple("${body.storeLocation} == 'london'"))
                    .marshal(new JacksonDataFormat(BookInfo.class))
                    .to("kafka:london-inventory")
                .when(simple("${body.storeLocation} == 'newyork'"))
                    .marshal(new JacksonDataFormat(BookInfo.class))
                    .to("kafka:newyork-inventory")
                .otherwise()
                    .log("No store location is defined for ${body}");
            ;
    }
}

