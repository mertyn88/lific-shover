package io.lific.data.request.consumer.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lific.data.shover.exceptions.schema.SchemaModelException;
import io.lific.data.shover.schema.SchemaModel;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentModel {

    private Long memberUid;
    private Long paymentId;
    private Long paymentAmount;
    private Long paymentPoint;
    private Integer couponUse;
    private Long paymentCoupon;
    private Long paymentTotalAmount;
    private String paymentMethod;
    private Integer paymentCount;
    private Integer paymentCancel;
    private Long branchId;
    private Long productId;
    private String productType;
    private Long majorCategoryId;
    private Long minorCategoryId;

    public static <T> SchemaModel.Builder convert(T data, SchemaModel.Builder schemaBuilder) {
        return convert(data).setAll(schemaBuilder.build());
    }

    public static <T> SchemaModel.Builder convert(T data) {
        PaymentModel model = null;
        try{
            model = SchemaModel.bind(data, PaymentModel.class);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert model != null;
        return convert(model);
    }

    public static SchemaModel.Builder convert(PaymentModel data) {
        SchemaModel.Builder builder = SchemaModel.builder();
        try{
            builder.put("member_uid", data.getMemberUid())
                   .put("payment_id", data.getPaymentId())
                   .put("payment_amount", data.getPaymentAmount())
                   .put("payment_point", data.getPaymentPoint())
                   .put("coupon_use", data.getCouponUse())
                   .put("payment_coupon", data.getPaymentCoupon())
                   .put("payment_total_amount", data.getPaymentTotalAmount())
                   .put("payment_method", data.getPaymentMethod())
                   .put("payment_count", data.getPaymentCount())
                   .put("payment_cancel", data.getPaymentCancel())
                   .put("branch_id", data.getBranchId())
                   .put("product_id", data.getProductId())
                   .put("product_type", data.getProductType())
                   .put("major_category_id", data.getMajorCategoryId())
                   .put("minor_category_id", data.getMinorCategoryId())
                   .put("datetime", LocalDateTime.now().toString());
        }catch (SchemaModelException e) {
            e.printStackTrace();
        }
        return builder;
    }
}
