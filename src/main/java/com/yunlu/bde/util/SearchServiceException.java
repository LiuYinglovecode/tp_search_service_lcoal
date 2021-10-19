package com.yunlu.bde.util;

public class SearchServiceException extends RuntimeException{
    private static final long serialVersionUID = 2743749858228956052L;
    public static final int ErrorOk = 0;
    public static final int IllegalArgumentException = 1;
    public static final int RuntimeException = 2;
    public static final int FailedToAssignUniqueIdException = 3;
    public static final int InternalException = 4;
    public static final int UnsupportedEncodingException = 5;
    public static final int ErrorUnknown = 999;
    private int code;

    public SearchServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = 4;
    }

    public SearchServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
