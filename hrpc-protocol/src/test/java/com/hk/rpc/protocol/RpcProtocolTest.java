package com.hk.rpc.protocol;

import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.header.RpcHeaderFactory;
import com.hk.rpc.protocol.request.RpcRequest;
import junit.framework.TestCase;

/**
 * @author : HK意境
 * @ClassName : RpcProtocolTest
 * @date : 2023/6/9 23:32
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcProtocolTest extends TestCase {

    public static RpcProtocol<RpcRequest> getRpcProtocol(){
        RpcHeader header = RpcHeaderFactory.getRequestHeader("jdk");
        RpcRequest body = new RpcRequest();
        body.setOneway(false);
        body.setAsync(false);
        body.setClassName("com.hk.rpc.demo.RpcProtocol");
        body.setMethodName("hello");
        body.setGroup("hk-hub");
        body.setParameters(new Object[]{"binghe"});
        body.setParameterTypes(new Class[]{String.class});
        body.setVersion("1.0.0");
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setBody(body);
        protocol.setHeader(header);
        return protocol;
    }



}