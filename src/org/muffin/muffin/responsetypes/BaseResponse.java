package org.muffin.muffin.responsetypes;

import lombok.Getter;

@Getter
class BaseResponse {
    static final int BAD_RESPONSE = -1;
    static final int BASE_RESPONSE = 0;
    static final int STRING_RESPONSE = 1;
    static final int OBJECT_RESPONSE = 2;
    static final int ARRAY_RESPONSE = 3;

    int status;
    String error;
}
