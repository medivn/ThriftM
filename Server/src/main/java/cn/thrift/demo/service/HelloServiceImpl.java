package cn.thrift.demo.service;
  
import cn.thrift.demo.helloworld.HelloWorld;
import org.apache.thrift.TException;
  
public class HelloServiceImpl implements HelloWorld.Iface {
    @Override
    public String sayHello(String username) throws TException {
        System.out.println("call Hello Service,"+username);
        return "hi,i am hello robot";
    }

    public String sayHelloSecond(String username, String argTwo) throws TException {
        return null;
    }
}