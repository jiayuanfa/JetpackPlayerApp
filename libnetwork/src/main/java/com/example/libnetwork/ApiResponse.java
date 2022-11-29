package com.example.libnetwork;

/**
 * 解析出来的的Model
 * @param <T>
 */
public class ApiResponse<T> {
    public boolean success;
    public int status;
    public String message;
    public T body;
}
