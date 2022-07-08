package io.lific.data;

import io.lific.data.request.consumer.benefit.BenefitCouponModel;
import io.lific.data.shover.ShoverInstance;
import io.lific.data.shover.config.Config;
import io.lific.data.shover.exceptions.SchemaException;

import java.util.List;

public class BenefitCouponTestMain {

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
                //.failStorage("/data")
                .build();

        ShoverInstance instance = new ShoverInstance(config);

        BenefitCouponModel.BenefitCouponModelBuilder builder =  BenefitCouponModel.builder()
                .couponId(999L)
                .couponIssueId(999L)
                .couponDisplayAmount(30L)
                .couponDiscountAmount(3000L)
                .couponUse(0)
                .couponSortation("WON")
                .couponType("TEST")
                .couponStatus("EXPIRE")
                ;

        System.out.println(BenefitCouponModel.convert(builder.build()).build());
        instance.sendTest("qa-data-benefit-coupon-consumer", BenefitCouponModel.convert(builder.build()).build());
    }
}
