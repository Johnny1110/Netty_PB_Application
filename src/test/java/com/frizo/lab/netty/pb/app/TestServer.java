package com.frizo.lab.netty.pb.app;

import com.frizo.lab.netty.pb.app.booter.ApplicationBooter;
import com.frizo.lab.netty.pb.app.proessor.RecordReader;
import com.frizo.lab.netty.pb.app.proto.ProtoData;
import com.google.protobuf.ByteString;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;

public class TestServer {

    private ApplicationBooter<ProtoData.Record> booter;

    private ProtoData.Record testRecord;

    private ProtoData.Record testStopRecord;

    private PbRecordSender pbRecordSender;

    @Before
    public void prepareServerSocket(){
        RecordReader<ProtoData.Record> reader = System.out::println;
        this.booter = new TestApplicationBooter(reader);
        this.pbRecordSender = new PbRecordSender();
        this.booter.addChannelActiveListener(this.pbRecordSender);
    }

    @Before
    public void prepateTestData(){
        ProtoData.PbData data1 = ProtoData.PbData.newBuilder()
                .setDataType(ProtoData.PbData.DataType.STRING)
                .setBinaryData(ByteString.copyFrom("Hello World!".getBytes()))
                .build();

        ProtoData.PbData data2 = ProtoData.PbData.newBuilder()
                .setDataType(ProtoData.PbData.DataType.INT)
                .setBinaryData(ByteString.copyFrom(ByteBuffer.allocate(4).putInt(1695609641).array()))
                .build();

        this.testRecord = ProtoData.Record.newBuilder()
                .setSignal(ProtoData.Record.Signal.NODE)
                .putColumn("data1", data1)
                .putColumn("data2", data2)
                .build();

        this.testStopRecord = ProtoData.Record.newBuilder()
                .setSignal(ProtoData.Record.Signal.STOP)
                .build();
    }

    @Test
    public void testServerSocket() throws InterruptedException {
        booter.startUp();
        pbRecordSender.addRecordToQueue(testRecord);
        pbRecordSender.addRecordToQueue(testRecord);
        pbRecordSender.addRecordToQueue(testRecord);


        //pbRecordSender.addRecordToQueue(testStopRecord);
//        Thread.sleep(5000);
//        booter.forceStopJob();
        while (true){

        }

    }

}
