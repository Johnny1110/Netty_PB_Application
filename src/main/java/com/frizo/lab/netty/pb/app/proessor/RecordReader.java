package com.frizo.lab.netty.pb.app.proessor;

public interface RecordReader<T> {
    void processRecord(T t);
}
