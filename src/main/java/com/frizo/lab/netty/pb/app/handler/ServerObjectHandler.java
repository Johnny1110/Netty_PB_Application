package com.frizo.lab.netty.pb.app.handler;

import com.frizo.lab.netty.pb.app.proessor.RecordReader;
import com.frizo.lab.netty.pb.app.proto.ProtoData;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class ServerObjectHandler extends SimpleChannelInboundHandler<Object> {

    private ChannelHandlerContext context;

    private RecordReader recordReader;

    private volatile boolean writable = false;

    public ServerObjectHandler(RecordReader recordReader) {
        this.recordReader = recordReader;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("connected with : " + ctx.channel().remoteAddress());
        this.writable = true;
        this.context = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        ProtoData.Record record = (ProtoData.Record) msg;
        if(!record.getSignal().equals(ProtoData.Record.Signal.STOP)){
            recordReader.processRecord(record);
        }else{
            System.out.println("Trying to close DMServer...");
            ctx.channel().parent().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    public void sendData(ProtoData.Record record){
        this.context.writeAndFlush(record);
    }

    public boolean isWritable(){
        return this.writable;
    }

    public void stopJob(){
        System.out.println("Trying to close client channel forcibly.");
        this.context.channel().close().addListener(ChannelFutureListener.CLOSE);
        this.context.channel().parent().close();
    }
}