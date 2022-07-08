package io.lific.data.request.consumer.benefit;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lific.data.shover.exceptions.schema.SchemaModelException;
import io.lific.data.shover.schema.SchemaModel;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BenefitCouponModel {

    private Long memberUid;
    private Long couponId;
    private Long couponIssueId;
    private Long couponDisplayAmount;
    private Long couponDiscountAmount;
    private Integer couponUse;
    private String couponSortation;
    private String couponType;
    private String couponStatus;

    public static <T> SchemaModel.Builder convert(T data, SchemaModel.Builder schemaBuilder) {
        return convert(data).setAll(schemaBuilder.build());
    }

    public static <T> SchemaModel.Builder convert(T data) {
        BenefitCouponModel model = null;
        try{
            model = SchemaModel.bind(data, BenefitCouponModel.class);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert model != null;
        return convert(model);
    }

    public static SchemaModel.Builder convert(BenefitCouponModel data) {
        SchemaModel.Builder builder = SchemaModel.builder();
        try{
            builder.put("member_uid", data.getMemberUid())
                   .put("coupon_id", data.getCouponId())
                   .put("coupon_issue_id", data.getCouponIssueId())
                   .put("coupon_display_amount", data.getCouponDisplayAmount())
                   .put("coupon_discount_amount", data.getCouponDiscountAmount())
                   .put("coupon_use", data.getCouponUse())
                   .put("coupon_sortation", data.getCouponSortation())
                   .put("coupon_type", data.getCouponType())
                   .put("coupon_status", data.getCouponStatus())
                   .put("datetime", LocalDateTime.now().toString());
        }catch (SchemaModelException e) {
            e.printStackTrace();
        }
        return builder;
    }
}
