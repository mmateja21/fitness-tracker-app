package com.example.fitnesstrackingapp.network;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Request<T> implements Serializable {



    private static final long serialVersionUID = 1L;

    private final RequestType type;

    private final T data;

    public Request(RequestType type){
        this(type, null);
    }

    public Request(RequestType type, T data){
        this.type=Objects.requireNonNull(
                type, "Vrsta zahteva ne sme biti null."
        );
        this.data=data;
    }

    public RequestType getType(){
        return type;
    }

    public T getData(){
        return data;
    }
}
