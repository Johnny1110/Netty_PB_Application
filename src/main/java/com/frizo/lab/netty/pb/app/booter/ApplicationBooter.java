package com.frizo.lab.netty.pb.app.booter;

public interface ApplicationBooter<T> {

    void startUp();

    void waitForConnection();

    void sendData(T t);

    void processEnd();

    void forceStopJob();

}
