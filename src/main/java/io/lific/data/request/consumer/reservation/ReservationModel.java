package io.lific.data.request.consumer.reservation;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lific.data.shover.exceptions.schema.SchemaModelException;
import io.lific.data.shover.schema.SchemaModel;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservationModel {

    private Long memberUid;
    private Long reservationId;
    private Integer reservationCount;
    private Integer reservationCancel;
    private Integer reservationPartnerCancel;
    private Integer reservationUpdate;
    private Integer reservationForm;
    private Long reservationStaffId;
    private Long branchId;
    private Long productId;
    private String productType;
    private Long majorCategoryId;
    private Long minorCategoryId;

    public static <T> SchemaModel.Builder convert(T data, SchemaModel.Builder schemaBuilder) {
        return convert(data).setAll(schemaBuilder.build());
    }

    public static <T> SchemaModel.Builder convert(T data) {
        ReservationModel model = null;
        try{
            model = SchemaModel.bind(data, ReservationModel.class);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert model != null;
        return convert(model);
    }

    public static SchemaModel.Builder convert(ReservationModel data) {
        SchemaModel.Builder builder = SchemaModel.builder();
        try{
            builder.put("member_uid", data.getMemberUid())
                   .put("reservation_id", data.getReservationId())
                   .put("reservation_count", data.getReservationCount())
                   .put("reservation_cancel", data.getReservationCancel())
                   .put("reservation_partner_cancel", data.getReservationPartnerCancel())
                   .put("reservation_update", data.getReservationUpdate())
                   .put("reservation_form", data.getReservationForm())
                   .put("reservation_staff_id", data.getReservationStaffId())
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
