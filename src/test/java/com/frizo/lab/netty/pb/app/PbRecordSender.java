package com.frizo.lab.netty.pb.app;

import com.frizo.lab.netty.pb.app.handler.ServerObjectHandler;
import com.frizo.lab.netty.pb.app.listener.ChannelActiveListener;
import com.frizo.lab.netty.pb.app.proto.ProtoData;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PbRecordSender implements ChannelActiveListener {

    private ConcurrentLinkedQueue<ProtoData.Record> recordQueue;

    public PbRecordSender(){
        recordQueue = new ConcurrentLinkedQueue<ProtoData.Record>();
    }

    public boolean addRecordToQueue(ProtoData.Record record){
        return recordQueue.offer(record);
    }

    @Override
    public void noticed(ServerObjectHandler serverObjectHandler) {
        while (true){
            ProtoData.Record record = recordQueue.poll();
            if (record == null){
                continue;
            }
            serverObjectHandler.sendData(record);
            if(record.getSignal().equals(ProtoData.Record.Signal.STOP)){
                break;
            }
        }
    }
}
