namespace java cn.thrift.demo.helloworld
const string VERSION = "1.0.1"
service HelloWorld{
 string sayHello(1:string username)
// string sayHelloSecond(1:string username,2:string argTwo)
}

service HandleJob{
 string doJob(1:string jobname)
// string doJobSecond(1:string jobname,2:string argTwo)
}