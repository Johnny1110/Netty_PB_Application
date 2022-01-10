package com.frizo.lab.netty.pb.app;

import com.frizo.lab.netty.pb.app.proto.ProtoData;
import com.google.protobuf.ByteString;
import org.junit.Test;

import java.nio.ByteBuffer;

public class ProtoDataTest {

    @Test
    public void testProtobuf(){

        //------------ 建立 node record 與 stop record -------------//
        ProtoData.PbData name = ProtoData.PbData.newBuilder()
                .setDataType(ProtoData.PbData.DataType.STRING)
                .setBinaryData(ByteString.copyFrom("Johnny".getBytes()))
                .build();

        ProtoData.PbData age = ProtoData.PbData.newBuilder()
                .setDataType(ProtoData.PbData.DataType.INT)
                .setBinaryData(ByteString.copyFrom(ByteBuffer.allocate(4).putInt(23).array()))
                .build();

        ProtoData.Record nodeRecord = ProtoData.Record.newBuilder()
                .setSignal(ProtoData.Record.Signal.NODE)
                .putColumn("name", name)
                .putColumn("age", age)
                .build();

        ProtoData.Record stopRecord = ProtoData.Record.newBuilder()
                .setSignal(ProtoData.Record.Signal.STOP)
                .build();

        //------------ 讀取 node record 與 stop record -------------//
        System.out.println("nodeRecord Signal: " + nodeRecord.getSignal());
        ProtoData.PbData data1 = nodeRecord.getColumnOrThrow("name");
        ProtoData.PbData data2 = nodeRecord.getColumnOrThrow("age");
        System.out.println("nodeRecord Column1<name> dataType: " + data1.getDataType());
        System.out.println("nodeRecord Column1<name> binaryData: " + data1.getBinaryData());
        System.out.println("nodeRecord Column1<age> dataType: " + data2.getDataType());
        System.out.println("nodeRecord Column1<age> binaryData: " + data2.getBinaryData());
        System.out.println("---------------------------------------------");
        System.out.println(stopRecord);
    }
}
