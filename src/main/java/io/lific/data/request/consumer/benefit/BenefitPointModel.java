package io.lific.data.request.consumer.benefit;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lific.data.shover.exceptions.schema.SchemaModelException;
import io.lific.data.shover.schema.SchemaModel;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BenefitPointModel {

    private Long memberUid;
    private Long pointId;
    private Long pointProvide;
    private String pointType;
    private Long branchId;
    private Long productId;

    public static <T> SchemaModel.Builder convert(T data, SchemaModel.Builder schemaBuilder) {
        return convert(data).setAll(schemaBuilder.build());
    }

    public static <T> SchemaModel.Builder convert(T data) {
        BenefitPointModel model = null;
        try{
            model = SchemaModel.bind(data, BenefitPointModel.class);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assert model != null;
        return convert(model);
    }

    public static SchemaModel.Builder convert(BenefitPointModel data) {
        SchemaModel.Builder builder = SchemaModel.builder();
        try{
            builder.put("member_uid", data.getMemberUid())
                   .put("point_id", data.getPointId())
                   .put("point_provide", data.getPointProvide())
                   .put("point_type", data.getPointType())
                   .put("branch_id", data.getBranchId())
                   .put("product_id", data.getProductId())
                   .put("datetime", LocalDateTime.now().toString());
        }catch (SchemaModelException e) {
            e.printStackTrace();
        }
        return builder;
    }
}
