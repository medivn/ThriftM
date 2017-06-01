package cn.thrift.demo.client;

import demo.helloworld.HandleJob;
import demo.helloworld.HelloWorld;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
  
public class HelloClient {  
  
    public void startClient() {  
        TTransport transport;  
        try {  
            transport = new TSocket("localhost", 10101);
            TProtocol protocol = new TBinaryProtocol(transport);
            TMultiplexedProtocol helloClient = new TMultiplexedProtocol(protocol,"helloworld");
            HelloWorld.Client client = new HelloWorld.Client(helloClient);

            TMultiplexedProtocol jobClient = new TMultiplexedProtocol(protocol,"handleJob");
            HandleJob.Client client1 = new HandleJob.Client(jobClient);

            transport.open();

            System.out.println(client.sayHello("hello"));
            System.out.println(client1.doJob("do job"));

            transport.close();  
        } catch (TTransportException e) {  
            e.printStackTrace();  
        } catch (TException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String[] args) {  
        HelloClient client = new HelloClient();  
        client.startClient();  
    }  
}