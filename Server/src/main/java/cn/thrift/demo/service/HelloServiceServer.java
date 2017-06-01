package cn.thrift.demo.service;

import cn.thrift.demo.helloworld.HandleJob;
import cn.thrift.demo.helloworld.HelloWorld;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TBinaryProtocol.Factory;  
import org.apache.thrift.server.TServer;  
import org.apache.thrift.server.TThreadPoolServer;  
import org.apache.thrift.server.TThreadPoolServer.Args;  
import org.apache.thrift.transport.TServerSocket;  
import org.apache.thrift.transport.TTransportException;  

public class HelloServiceServer {  
    public void startServer() {  
        try {  
  
            TServerSocket serverTransport = new TServerSocket(1234);
            TMultiplexedProcessor processor = new TMultiplexedProcessor();

            processor.registerProcessor("helloworld",new HelloWorld.Processor(new HelloServiceImpl()));

            processor.registerProcessor("handleJob",new HandleJob.Processor(new JobServiceImpl()));

            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            server.serve();

        } catch (TTransportException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String[] args) {  
        HelloServiceServer server = new HelloServiceServer();  
        server.startServer();  
    }  
}  