

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;



@BTrace

public class HelloWorldScript {


    //最简单的拦截方法, 针对某个类的 start 方法
    @OnMethod(clazz="java.lang.Thread", method="start")
    public static void onThreadStart() {

        println("thread start!");

    }


    //根据某个父类监听各个实现类的的方法
    @OnMethod(clazz="+java.lang.Runnable", method="run")
    public static void onRunCall(@ProbeClassName String probeClass){
        println("Run call class : " + probeClass);
    }


    //检查某个方法的执行时间,需要添加拦截时机
    @OnMethod(clazz = "org.xgh.btrace.app.HelloWorldRunnable", method = "run", location = @Location(Kind.RETURN))
    public static void onRunReturn(@Duration long duration){
        println("Thread run duration: " +  (duration/1000000));
    }

    //监控某个方法的上下文, 它调用了哪些其他的方法
    @OnMethod(clazz = "org.xgh.btrace.app.HelloWorldRunnable", method = "run",
            location = @Location(value = Kind.CALL, clazz = "/.*/", method = "/.*/", where = Where.AFTER))
    public static void onRunIndirectCall(@Self Object self, @TargetInstance Object instance,
                              @TargetMethodOrField String method, @Duration long duration){
        println("Self object : " + self);
        println("target instance : " + instance);
        println("target method : " + method);
        println("target run duration : " + duration);

    }

    //典型场景一, 找到调用延时超过1ms 的方法, 但不能跟onRunIndirectCall 一起使用
    @OnMethod(clazz = "org.xgh.btrace.app.HelloWorldRunnable", method = "/.*/", location = @Location(Kind.RETURN))
    public static void onSlowInvocation(@ProbeClassName String pcn,  @Duration long duration) {
        if (duration > 1000000) {
            println(pcn + ",slow invocation duration:" + (duration / 100000));

        }

    }

    //典型场景二, 谁调用了这个方法
    @OnMethod(clazz = "org.xgh.btrace.app.HelloWorldMainService", method = "method1")
    public static void onServiceMethod() {

        println("entered service Method");
        jstack();

    }






}
