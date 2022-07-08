package io.lific.data;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lific.data.request.consumer.reservation.ReservationModel;
import io.lific.data.shover.ShoverInstance;
import io.lific.data.shover.config.Config;
import io.lific.data.shover.exceptions.SchemaException;

import java.util.List;

public class TestMain {

    // DEV
    //private static final List<String> SERVER = List.of("b-1.dev-lific-msk.w3yo3o.c2.kafka.ap-northeast-2.amazonaws.com:9092", "b-2.dev-lific-msk.w3yo3o.c2.kafka.ap-northeast-2.amazonaws.com:9092");
    // QA
    private static final List<String> SERVER = List.of("b-1.qa-lific-msk.wye3ft.c2.kafka.ap-northeast-2.amazonaws.com:9092", "b-2.qa-lific-msk.wye3ft.c2.kafka.ap-northeast-2.amazonaws.com:9092");

    public static void main(String[] args) throws SchemaException {

        // TODO - Spring @Configuration 위치
        Config config = new Config.Builder()
                .bootstrapServersConfig(SERVER)
                .clientId("test")
                .keySerializerClass()
                .valueSerializerClass()
                .schemaRegistryUrl("https://qa-schema-registry.lific.net")
                .failStorage("/data")
                .build();

        ShoverInstance instance = new ShoverInstance(config);

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put("id", "id1");
        json.put("date", "date1");

        System.out.println(json);
        instance.sendTest("test-hadoop-hdfs", json);
    }
}
