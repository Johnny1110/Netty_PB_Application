package com.frizo.lab.netty.pb.app.booter;

import com.frizo.lab.netty.pb.app.listener.ChannelActiveListener;
import com.frizo.lab.netty.pb.app.listener.ProcessEndListener;

public interface ApplicationBooter<T> {

    void startUp();

    @Deprecated
    void waitForConnection();

    @Deprecated
    void sendData(T t);

    @Deprecated
    void processEnd();

    void forceStopJob();

    void addProcessEndListener(ProcessEndListener listener);

    void removeProcessEndListener(ProcessEndListener listener);

    void addChannelActiveListener(ChannelActiveListener listener);

    void removeChannelActiveListener(ChannelActiveListener listener);

    boolean isConnected();

}
