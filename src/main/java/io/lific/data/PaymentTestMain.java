package io.lific.data;

import io.lific.data.request.consumer.payment.PaymentModel;
import io.lific.data.request.consumer.reservation.ReservationModel;
import io.lific.data.shover.ShoverInstance;
import io.lific.data.shover.config.Config;
import io.lific.data.shover.exceptions.SchemaException;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public class PaymentTestMain {

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

        PaymentModel.PaymentModelBuilder builder = PaymentModel.builder()
                .memberUid(997L)
                .paymentId(999L)
                .paymentAmount(999L)
                .paymentPoint(0L)
                .couponUse(0)
                .paymentTotalAmount(999L)
                .paymentMethod("TEST")
                .paymentCount(0)
                .paymentCancel(0)
                .branchId(999L)
                .productId(999L)
                .productType("TEST")
                .majorCategoryId(999L)
                .minorCategoryId(999L)
                ;

        System.out.println(PaymentModel.convert(builder.build()).build());
        instance.sendTest("qa-data-payment-consumer", PaymentModel.convert(builder.build()).build());
    }
}
