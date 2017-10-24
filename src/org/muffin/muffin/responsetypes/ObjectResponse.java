package org.muffin.muffin.responsetypes;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ObjectResponse extends BaseResponse {
    private JsonObject data;

    private ObjectResponse() {
    }

    public static ObjectResponse get(@NonNull final JsonObject data) {
        ObjectResponse response = new ObjectResponse();
        response.status = BaseResponse.OBJECT_RESPONSE;
        response.data = data;
        return response;
    }

    public static ObjectResponse error(@NonNull final String error) {
        ObjectResponse response = new ObjectResponse();
        response.status = BaseResponse.BAD_RESPONSE;
        response.error = error;
        return response;
    }
}
