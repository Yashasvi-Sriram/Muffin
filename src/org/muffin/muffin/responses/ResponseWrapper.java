package org.muffin.muffin.responses;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ResponseWrapper<D, E> {
    public static final int OBJECT_RESPONSE = 1;
    public static final int ARRAY_RESPONSE = 2;
    public static final int STRING_RESPONSE = 3;
    public static final int NUMBER_RESPONSE = 4;
    public static final int BOOLEAN_RESPONSE = 5;
    public static final int CHAR_RESPONSE = 6;

    private static final int ERROR_RESPONSE = -1;
    private static final int EMPTY_RESPONSE = 0;

    private int status;
    private E error;
    private D data;

    private ResponseWrapper() {
    }

    public static <D, E> ResponseWrapper<D, E> get(@NonNull final D data, final int status) {
        ResponseWrapper<D, E> responseWrapper = new ResponseWrapper<>();
        responseWrapper.data = data;
        responseWrapper.status = status;
        return responseWrapper;
    }

    public static <D, E> ResponseWrapper<D, E> error(@NonNull final E error) {
        ResponseWrapper<D, E> responseWrapper = new ResponseWrapper<>();
        responseWrapper.status = ERROR_RESPONSE;
        responseWrapper.error = error;
        return responseWrapper;
    }
}
