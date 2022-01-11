package com.frizo.lab.netty.pb.app.listener;

public interface ApplicationListener<T> {

    void noticed(T t);

}
