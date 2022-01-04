package com.frizo.lab.netty.pb.app.booter;

import com.frizo.lab.netty.pb.app.handler.ServerObjectHandler;
import com.frizo.lab.netty.pb.app.handler.ServerProtoBufInitializer;
import com.frizo.lab.netty.pb.app.proessor.RecordReader;
import com.frizo.lab.netty.pb.app.proto.ProtoData;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public abstract class AbstractApplicationBooter implements ApplicationBooter<ProtoData.Record> {

    private ServerObjectHandler serverObjectHandler;

    public AbstractApplicationBooter(RecordReader recordReader){
        this.serverObjectHandler = new ServerObjectHandler(recordReader);
    }

    @Override
    public void startUp() {
        new Thread(() ->{
            EventLoopGroup group = new NioEventLoopGroup(1);
            try{
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(group)
                        .channel(NioServerSocketChannel.class)
                        .localAddress(new InetSocketAddress("127.0.0.1", 0))
                        .childHandler(new ServerProtoBufInitializer(serverObjectHandler));
                ChannelFuture future = bootstrap.bind().sync();
                int port = ((InetSocketAddress) future.channel().localAddress()).getPort();
                String hostname = ((InetSocketAddress) future.channel().localAddress()).getHostName();

                runClient(hostname, port);

                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
                System.out.println("DMServer gracefully shutdown.");
            }
        }).start();
    }

    public abstract void runClient(String remoteHostName, int remotePort);

    @Override
    public void sendData(ProtoData.Record record){
        this.serverObjectHandler.sendData(record);
    }

    @Override
    public void waitForConnection(){
        while (!this.serverObjectHandler.isWritable());
    }

    @Override
    public void processEnd() {
        ProtoData.Record stop = ProtoData.Record.newBuilder()
                .setSignal(ProtoData.Record.Signal.STOP)
                .build();
        sendData(stop);
    }

    @Override
    public void forceStopJob(){
        serverObjectHandler.stopJob();
    }

}
