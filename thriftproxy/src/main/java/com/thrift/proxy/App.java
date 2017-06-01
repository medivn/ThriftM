package com.thrift.proxy;

import com.facebook.nifty.codec.DefaultThriftFrameCodecFactory;
import com.facebook.nifty.codec.ThriftFrameCodecFactory;
import com.facebook.nifty.duplex.TDuplexProtocolFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final int MAX_FRAME_SIZE = 64 * 1024 * 1024;

    static TDuplexProtocolFactory tDuplexProtocolFactory = TDuplexProtocolFactory.fromSingleFactory(new TBinaryProtocol.Factory(true, true));

    public static void main(String[] args) throws Exception {

        ServerBootstrap bootstrap = new ServerBootstrap();
        // boss线程监听端口，worker线程负责数据读写
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService worker = Executors.newCachedThreadPool();
        // 设置niosocket工厂
        bootstrap.setFactory(new NioServerSocketChannelFactory(boss, worker));
        // 设置管道的工厂
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();

                TProtocolFactory inputProtocolFactory = tDuplexProtocolFactory.getInputProtocolFactory();
                ThriftFrameCodecFactory thriftFrameCodecFactory = new DefaultThriftFrameCodecFactory();
                pipeline.addLast("frameCodec", thriftFrameCodecFactory.create(MAX_FRAME_SIZE,
                        inputProtocolFactory));
                pipeline.addLast("helloHandler", new SocksServerConnectHandler());
                return pipeline;
            }
        });
        bootstrap.bind(new InetSocketAddress(10101));
        System.out.println("server1 start!!!");
    }



}
