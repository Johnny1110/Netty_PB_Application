package com.frizo.lab.netty.pb.app.handler;

import com.frizo.lab.netty.pb.app.proto.ProtoData;
import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ServerProtoBufInitializer  extends ChannelInitializer<Channel> {

    private MessageLite lite;
    private ServerObjectHandler serverObjectHandler;

    public ServerProtoBufInitializer(ServerObjectHandler serverObjectHandler) {
        this.lite = ProtoData.Record.getDefaultInstance();
        this.serverObjectHandler = serverObjectHandler;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(lite));
        pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());
        pipeline.addLast("messageHandler", serverObjectHandler);
    }

}
