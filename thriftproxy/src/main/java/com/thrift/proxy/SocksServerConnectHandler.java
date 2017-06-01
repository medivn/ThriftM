package com.thrift.proxy;

import com.facebook.nifty.core.ThriftMessage;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.socks.SocksCmdRequest;
import org.jboss.netty.handler.codec.socks.SocksCmdResponse;
import org.jboss.netty.handler.codec.socks.SocksMessage;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author yihua.huang@dianping.com
 */
public class SocksServerConnectHandler extends SimpleChannelUpstreamHandler {

	private static final String name = "SOCKS_SERVER_CONNECT_HANDLER";

	public static String getName() {
		return name;
	}

	private final ClientSocketChannelFactory cf;

	private volatile Channel outboundChannel;

	final Object trafficLock = new Object();

	public SocksServerConnectHandler() {
		Executor executor = Executors.newCachedThreadPool();
		Executor executorWorker = Executors.newCachedThreadPool();

		// Set up the event pipeline factory.
		ClientSocketChannelFactory cf =
				new NioClientSocketChannelFactory(executor, executorWorker);
		this.cf = cf;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		final ThriftMessage message = (ThriftMessage) e.getMessage();
		final Channel inChannel = ctx.getChannel();
		// 同服务端相同，只是这里使用的是NioClientSocketChannelFactory
		final ChannelFactory factory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool(),
				8);

		// ClientBootstrap用于帮助客户端启动
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		// 由于客户端不包含ServerSocketChannel，所以参数名不能带有child.前缀
		bootstrap.setOption("tcpNoDelay", true);
//      bootstrap.setOption("keepAlive", true);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline channelPipeline =
						Channels.pipeline(new ClientLogicHandler(message,inChannel));

				System.out.println(channelPipeline.hashCode());
				return channelPipeline;
			}
		});

		// 这里连接服务端绑定的IP和端口
		bootstrap.connect(new InetSocketAddress("127.0.0.1", 1234));
		System.out.println("Client is started...");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		super.exceptionCaught(ctx, e);
	}

	public class ClientLogicHandler extends SimpleChannelHandler {

		ThriftMessage message;

		Channel inChannel;

		public ClientLogicHandler(ThriftMessage message, Channel inChannel) {
			this.message = message;
			this.inChannel = inChannel;
		}


		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			System.out.println("######channelConnected");

			Channel ch = e.getChannel();
			ch.write(message.getBuffer());
		}

		@Override
		public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e)
				throws Exception {
			System.out.println("######writeComplete");
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			System.out.println("######messageReceived");

			inChannel.write(e.getMessage());

			ChannelFuture channelFuture = e.getChannel().close();
			channelFuture.addListener(ChannelFutureListener.CLOSE);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
			Channel ch = e.getChannel();
			ch.close();
		}
	}

	/**
	 * Closes the specified channel after all queued write requests are flushed.
	 */
	static void closeOnFlush(Channel ch) {
		if (ch.isConnected()) {
			ch.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
