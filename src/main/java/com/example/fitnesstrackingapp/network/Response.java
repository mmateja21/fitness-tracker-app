package com.example.fitnesstrackingapp.network;
import java.io.Serial;
import java.io.Serializable;

public class Response<T> implements Serializable {



    private static final long serialVersionUID = 1L;

    private final boolean successful;

    private final String message;

    private final T data;

    public Response(
            boolean successful,
            String message,
            T data
    ){
        this.successful=successful;
        this.message=message;
        this.data=data;
    }

    public static <T> Response <T> success(
            String message, T data
    ){
        return new Response<>(true, message, data);
    }

    public static Response<Void> success(String message){
        return new Response<>(true, message, null);
    }

    public static Response<Void> failure (String message){

        return new Response<>(false, message, null);
    }

    public boolean isSuccessful(){
        return successful;
    }

    public String getMessage(){
        return message;
    }
    public T getData(){
        return data;
    }
}
