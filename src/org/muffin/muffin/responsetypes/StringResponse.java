package org.muffin.muffin.responsetypes;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class StringResponse extends BaseResponse {
    private String data;

    private StringResponse() {
    }

    public static StringResponse get(@NonNull final String data) {
        StringResponse response = new StringResponse();
        response.status = BaseResponse.STRING_RESPONSE;
        response.data = data;
        return response;
    }

    public static StringResponse error(@NonNull final String error) {
        StringResponse response = new StringResponse();
        response.status = BaseResponse.BAD_RESPONSE;
        response.error = error;
        return response;
    }
}
