package com.frizo.lab.netty.pb.app;

import com.frizo.lab.netty.pb.app.booter.AbstractApplicationBooter;
import com.frizo.lab.netty.pb.app.proessor.RecordReader;
import com.frizo.lab.netty.pb.app.proto.ProtoData;

public class TestApplicationBooter extends AbstractApplicationBooter {

    public TestApplicationBooter(RecordReader<ProtoData.Record> recordReader) {
        super(recordReader);
    }

    @Override
    public void runClient(String remoteHostName, int remotePort) {
        System.out.println("please run the client socket, remoteHostName: " + remoteHostName + " remotePort: " + remotePort);
    }
}
