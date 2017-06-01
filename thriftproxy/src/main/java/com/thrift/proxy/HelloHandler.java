package com.thrift.proxy;

import com.facebook.nifty.core.TNiftyTransport;
import com.facebook.nifty.core.ThriftMessage;
import com.facebook.nifty.duplex.TProtocolPair;
import com.facebook.nifty.duplex.TTransportPair;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;  
  
// 消息接受处理类  
public class HelloHandler extends SimpleChannelHandler {  
  
    // 接收消息  
    @Override  
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)  
            throws Exception {
        if (e.getMessage() instanceof ThriftMessage) {
            ThriftMessage message = (ThriftMessage) e.getMessage();
            message.setProcessStartTimeMillis(System.currentTimeMillis());

            TNiftyTransport messageTransport = new TNiftyTransport(ctx.getChannel(), message);
            TTransportPair transportPair = TTransportPair.fromSingleTransport(messageTransport);
            TProtocolPair protocolPair = App.tDuplexProtocolFactory.getProtocolPair(transportPair);
            TProtocol inProtocol = protocolPair.getInputProtocol();
            TProtocol outProtocol = protocolPair.getOutputProtocol();
            TMessage tmessage = inProtocol.readMessageBegin();
            System.out.println(tmessage.name);

        }
    }  
  

}  