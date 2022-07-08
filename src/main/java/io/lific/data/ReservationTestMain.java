package io.lific.data;

import io.lific.data.request.consumer.reservation.ReservationModel;
import io.lific.data.shover.ShoverInstance;
import io.lific.data.shover.config.Config;
import io.lific.data.shover.exceptions.SchemaException;

import java.util.List;

public class ReservationTestMain {

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

        ReservationModel.ReservationModelBuilder builder = ReservationModel.builder()
                .memberUid(999L)
                .reservationId(999L)
                .reservationCount(3)
                .reservationCancel(0)
                .reservationPartnerCancel(0)
                .reservationStaffId(13L)
                .reservationForm(1)
                .branchId(999L)
                .productId(999L)
                .productType("TEST")
                .majorCategoryId(999L)
                .minorCategoryId(999L)
                ;

        //ObjectNode json = JsonNodeFactory.instance.objectNode();
        //json.put("name", "value");
        //json.put("address1", "value");
        //json.put("address1", "value");
        //json.put("test", "value");
        System.out.println(ReservationModel.convert(builder.build()).build());
        instance.sendTest("qa-data-reservation-consumer", ReservationModel.convert(builder.build()).build());
    }
}
