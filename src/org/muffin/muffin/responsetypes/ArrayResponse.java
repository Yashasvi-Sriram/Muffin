package org.muffin.muffin.responsetypes;

import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ArrayResponse extends BaseResponse {
    private JsonArray data;

    private ArrayResponse() {
    }

    public static ArrayResponse get(@NonNull final JsonArray data) {
        ArrayResponse response = new ArrayResponse();
        response.status = BaseResponse.ARRAY_RESPONSE;
        response.data = data;
        return response;
    }

    public static ArrayResponse error(@NonNull final String error) {
        ArrayResponse response = new ArrayResponse();
        response.status = BaseResponse.BAD_RESPONSE;
        response.error = error;
        return response;
    }
}
