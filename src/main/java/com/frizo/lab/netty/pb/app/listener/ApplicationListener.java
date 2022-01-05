package com.frizo.lab.netty.pb.app.listener;

import io.netty.channel.Channel;

public interface ApplicationListener<T> {

    void noticed(T t);

}
