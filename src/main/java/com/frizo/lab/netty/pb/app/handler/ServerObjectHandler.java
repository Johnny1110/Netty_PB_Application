package com.frizo.lab.netty.pb.app.handler;

import com.frizo.lab.netty.pb.app.listener.ChannelActiveListener;
import com.frizo.lab.netty.pb.app.listener.ProcessEndListener;
import com.frizo.lab.netty.pb.app.proessor.RecordReader;
import com.frizo.lab.netty.pb.app.proto.ProtoData;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;


public class ServerObjectHandler extends SimpleChannelInboundHandler<Object> {

    private ChannelHandlerContext context;

    private RecordReader<ProtoData.Record> recordReader;

    private volatile boolean writable = false;

    private List<ProcessEndListener> endListenerList = new ArrayList<>();

    private List<ChannelActiveListener> channelActiveListenerList = new ArrayList<>();

    public ServerObjectHandler(RecordReader recordReader) {
        this.recordReader = recordReader;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("connected with : " + ctx.channel().remoteAddress());
        this.writable = true;
        this.context = ctx;
        if (!this.channelActiveListenerList.isEmpty()) {
            this.channelActiveListenerList.forEach(listener -> {
                listener.noticed(this);
            });
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        ProtoData.Record record = (ProtoData.Record) msg;
        if(!record.getSignal().equals(ProtoData.Record.Signal.STOP)){
            recordReader.processRecord(record);
        }else{
            System.out.println("Reviced close signal, notice endListeners...");
            this.writable = false;
            if (!this.endListenerList.isEmpty()) {
                this.endListenerList.forEach(listener -> {
                    listener.noticed(ctx.channel().parent());
                });
            }
            System.out.println("Trying to close DMServer...");
            stopJob();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.err.println("Connection with client encounter some problem, trying to close server...");
        cause.printStackTrace();
        stopJob();
    }

    public void sendData(ProtoData.Record record){
        this.context.writeAndFlush(record);
    }

    public boolean isWritable(){
        return this.writable;
    }

    public void stopJob(){
        this.writable = false;
        if(this.context!=null) {
            if (context.channel().isActive()){
                System.out.println("Trying to close client channel forcibly...");
                this.context.channel().close().addListener(ChannelFutureListener.CLOSE);
                System.out.println("Client channel closed successfully.");
            }
            this.context.channel().parent().close();
            System.out.println("Main channel closed successfully.");

        }
        this.context = null;
    }

    public void addProcessEndListener(ProcessEndListener listener){
        this.endListenerList.add(listener);
    }

    public void removeProcessEndListener(ProcessEndListener listener){
        this.endListenerList.remove(listener);
    }

    public void addChannelActiveListener(ChannelActiveListener listener){
        this.channelActiveListenerList.add(listener);
    }

    public void removeChannelActiveListener(ChannelActiveListener listener){
        this.channelActiveListenerList.remove(listener);
    }

}