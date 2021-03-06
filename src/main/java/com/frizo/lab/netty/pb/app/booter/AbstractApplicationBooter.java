package com.frizo.lab.netty.pb.app.booter;

import com.frizo.lab.netty.pb.app.listener.ChannelActiveListener;
import com.frizo.lab.netty.pb.app.listener.ProcessEndListener;
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

    private EventLoopGroup bossGroup;

    //private EventLoopGroup workerGroup;

    public AbstractApplicationBooter(RecordReader recordReader){
        this.serverObjectHandler = new ServerObjectHandler(recordReader);
        this.bossGroup = new NioEventLoopGroup(1);
        //this.workerGroup = new NioEventLoopGroup(1);
    }

    @Override
    final public void addProcessEndListener(ProcessEndListener listener){
        this.serverObjectHandler.addProcessEndListener(listener);
    }

    @Override
    final public void removeProcessEndListener(ProcessEndListener listener){
        this.serverObjectHandler.removeProcessEndListener(listener);
    }

    @Override
    final public void addChannelActiveListener(ChannelActiveListener listener){
        this.serverObjectHandler.addChannelActiveListener(listener);
    }

    @Override
    final public void removeChannelActiveListener(ChannelActiveListener listener){
        this.serverObjectHandler.removeChannelActiveListener(listener);
    }

    @Override
    public void startUp() {
        Thread booterThread = new Thread(() ->{
            try{
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap
                        //.group(bossGroup, workerGroup)
                        .group(bossGroup)
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
                this.bossGroup.shutdownGracefully();
                //this.workerGroup.shutdownGracefully();
                System.out.println("DMServer gracefully shutdown.");
            }
        });

        booterThread.setDaemon(true);
        booterThread.setName("Application-Booter-Thread");
        booterThread.start();
    }

    // ?????? python ?????????
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
        this.bossGroup.shutdownGracefully();
        //this.workerGroup.shutdownGracefully();
    }

    @Override
    public boolean isConnected(){
        return serverObjectHandler.isWritable();
    }

}
