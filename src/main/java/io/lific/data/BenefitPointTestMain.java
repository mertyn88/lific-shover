package io.lific.data;

import io.lific.data.request.consumer.benefit.BenefitPointModel;
import io.lific.data.request.consumer.reservation.ReservationModel;
import io.lific.data.shover.ShoverInstance;
import io.lific.data.shover.config.Config;
import io.lific.data.shover.exceptions.SchemaException;

import java.util.List;

public class BenefitPointTestMain {

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
                .build();

        ShoverInstance instance = new ShoverInstance(config);

        BenefitPointModel.BenefitPointModelBuilder builder = BenefitPointModel.builder()
                .memberUid(999L)
                .pointId(999L)
                .pointProvide(999L)
                .pointType("TEST")
                .productId(999L)
                .branchId(999L)
                ;

        System.out.println(BenefitPointModel.convert(builder.build()).build());
        instance.sendTest("qa-data-benefit-point-consumer", BenefitPointModel.convert(builder.build()).build());
    }
}
