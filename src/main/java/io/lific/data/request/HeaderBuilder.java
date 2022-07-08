package io.lific.data.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lific.data.shover.exceptions.schema.SchemaModelException;
import io.lific.data.shover.schema.SchemaModel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HeaderBuilder {
    private String forwardFor;
    private String tuuid;
    private String domain;
    private String os;
    private String device;
    private PartnerAccessToken partnerAccessToken;
    private ConsumerAccessToken consumerAccessToken;
    private OperatorAccessToken operatorAccessToken;
    private String timezone;
    private String languageCode;
    private String countryCode;

    @Getter @Setter
    public static class AccessToken {
        private Long memberUid;
        private String memberId;
    }
    @Getter @Setter
    public static class PartnerAccessToken extends AccessToken {
        private Long businessId;
        private Long branchId;
    }
    @Getter @Setter public static class ConsumerAccessToken extends AccessToken { }
    @Getter @Setter public static class OperatorAccessToken extends AccessToken { }

    public static <T> SchemaModel.Builder builder(T data, SchemaModel.Builder schemaBuilder) {
        return builder(data).setAll(schemaBuilder.build());
    }

    public static <T> SchemaModel.Builder builder(T data) {
        HeaderBuilder header = null;
        try{
            header = SchemaModel.bind(data, HeaderBuilder.class);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        SchemaModel.Builder builder = SchemaModel.builder();
        if(header != null) {
            try{
                builder.put("FORWORD_FOR", header.getForwardFor())
                       .put("TUUID", header.getTuuid())
                       .put("DOMAIN", header.getDomain())
                       .put("OS", header.getOs())
                       .put("DEVICE", header.getDevice())
                       .put("TIMEZONE", header.getTimezone())
                       .put("LANGUAGE_CODE", header.getLanguageCode())
                       .put("COUNTRY_CODE", header.getCountryCode());
                if(header.getPartnerAccessToken() != null) {
                    builder.put("MEMBER_UID", header.getPartnerAccessToken().getMemberUid());
                    builder.put("MEMBER_ID", header.getPartnerAccessToken().getMemberId());
                    builder.put("BUSINESS_ID", header.getPartnerAccessToken().getBusinessId());
                    builder.put("BRANCH_ID", header.getPartnerAccessToken().getBranchId());
                }else if(header.getConsumerAccessToken() != null) {
                    builder.put("MEMBER_UID", header.getConsumerAccessToken().getMemberUid());
                    builder.put("MEMBER_ID", header.getConsumerAccessToken().getMemberId());
                }else if(header.getOperatorAccessToken() != null) {
                    builder.put("MEMBER_UID", header.getOperatorAccessToken().getMemberUid());
                    builder.put("MEMBER_ID", header.getOperatorAccessToken().getMemberId());
                }
            }catch (SchemaModelException e) {
                e.printStackTrace();
            }
        }

        return builder;
    }
}
