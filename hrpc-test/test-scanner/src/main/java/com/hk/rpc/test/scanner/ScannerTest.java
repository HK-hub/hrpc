package com.hk.rpc.test.scanner;

import com.hk.rpc.common.scanner.ClassScanner;
import com.hk.rpc.common.scanner.reference.RpcReferenceScanner;
import com.hk.rpc.provider.common.scanner.RpcServiceScanner;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author : HK意境
 * @ClassName : ScannerTest
 * @date : 2023/6/8 22:10
 * @description : 用户测试扫描逻辑
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ScannerTest {

    private final String packageName = "com.hk.rpc.test.scanner";


    /**
     * 测试扫描 com.hk.rpc.test.scanner 包下的类
     */
    @Test
    public void testScannerClassNameList() throws IOException {

        List<String> classNameList = ClassScanner.getClassNameList(packageName);
        classNameList.forEach(System.out::println);
    }



    /**
     * 测试 @RpcService 注解扫描器
     */
    @Test
    public void testScannerClassNameListByRpcService() throws IOException {

        RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService("",1, packageName, null);
    }

    /**
     * 测试 @RpcReference 注解扫描器
     * @throws IOException
     */
    @Test
    public void testScannerClassNameListByRpcReference() throws IOException {

        RpcReferenceScanner.doScannerWithRpcReferenceAnnotationFilter("",1, packageName);
    }



}
