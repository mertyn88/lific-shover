package io.lific.data.request.consumer.infinity;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.lific.data.shover.exceptions.schema.SchemaModelException;
import io.lific.data.shover.schema.SchemaModel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SearchModel {

    private String term;
    private String sort;
    private Integer page;
    private GeoLocation geoLocation;

    @Getter @Setter
    public static class GeoLocation {
        private Double lat;
        private Double lon;
    }

    public static <T> SchemaModel.Builder builder(T data, SchemaModel.Builder schemaBuilder) {
        return builder(data).setAll(schemaBuilder.build());
    }

    public static <T> SchemaModel.Builder builder(T data) {
        SearchModel search = null;
        try{
            search = SchemaModel.bind(data, SearchModel.class);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        SchemaModel.Builder builder = SchemaModel.builder();
        if(search != null) {
            try{
                builder.put("KEYWORD", search.getTerm())
                       .put("SORT", search.getSort())
                       .put("PAGE", search.getPage());
                if(search.getGeoLocation() != null) {
                    builder.put("GEOLOCATION_LAT", search.getGeoLocation().getLat());
                    builder.put("GEOLOCATION_LON", search.getGeoLocation().getLon());
                }
            }catch (SchemaModelException e) {
                e.printStackTrace();
            }
        }
        return builder;
    }
}
