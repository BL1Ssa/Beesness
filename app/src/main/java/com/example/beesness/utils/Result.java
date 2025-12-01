package com.example.beesness.utils;

import com.example.beesness.R;

public class Result<T> {
    public enum Status {LOADING, SUCCESS, ERROR};

    public final Status status;
    public final T data;
    public final String message;

    public Result(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> loading(){
        return new Result<>(Status.LOADING, null, null);
    }

    public static <T> Result<T> success(T data){
        return new Result<>(Status.SUCCESS, data, null);
    }

    public static <T> Result<T> success(T data, String msg){
        return new Result<>(Status.SUCCESS, data, msg);
    }

    public static <T> Result<T> error(String msg){
        return new Result<>(Status.ERROR, null, msg);
    }
}
