package org.muffin.muffin.responses;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class GenericResponse<D, E> {
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

    private GenericResponse() {
    }

    public static <D, E> GenericResponse<D, E> get(@NonNull final D data, final int status) {
        GenericResponse<D, E> genericResponse = new GenericResponse<>();
        genericResponse.data = data;
        genericResponse.status = status;
        return genericResponse;
    }

    public static <D, E> GenericResponse<D, E> error(@NonNull final E error) {
        GenericResponse<D, E> genericResponse = new GenericResponse<>();
        genericResponse.status = ERROR_RESPONSE;
        genericResponse.error = error;
        return genericResponse;
    }
}
