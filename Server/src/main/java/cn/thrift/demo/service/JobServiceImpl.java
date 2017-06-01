package cn.thrift.demo.service;

import cn.thrift.demo.helloworld.HandleJob;
import org.apache.thrift.TException;

public class JobServiceImpl implements HandleJob.Iface {
    public String doJobSecond(String jobname, String argTwo) throws TException {
        return null;
    }

    @Override
    public String doJob(String username) throws TException {
        System.out.println("call job Service,"+username);
        return "hi,i am job robot";
    }
}