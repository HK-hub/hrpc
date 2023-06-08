package com.hk.rpc.common.scanner;

import java.io.File;
import java.util.List;

/**
 * @author : HK意境
 * @ClassName : ClassScanner
 * @date : 2023/6/8 15:26
 * @description : 通用类扫描器
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ClassScanner {

    /**
     * 文件：扫描当前工程中指定包下的所有类信息
     */
    private static final String PROTOCOL_FILE = "file";


    /**
     * jar包：扫描jar文件中指定包下的所有类信息
     */
    private static final String PROTOCOL_JAR = "jar";

    /**
     * class文件后缀：扫描的过程中指定需要处理的文件的后缀信息
     */
    private static final String CLASS_FILE_SUFFIX = ".class";


    /**
     * 扫描当前工程下指定包下的所有类信息
     * @param packageName   扫描的包名称
     * @param packagePath   包在磁盘上的完整路径
     * @param recursive     是否递归调用
     * @param classNameList 类名称的集合
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<String> classNameList) {

        // 获取此包的目录，建立一个 file
        File file = new File(packagePath);

        // 不存在，不是目录直接返回
        if (!file.exists() || !file.isDirectory()) {
            return;
        }

        // 如果存在就获取包下的所有文件
        File[] files = file.listFiles(pathname -> {
            // 自定义过滤规则：如果可以循环或者是 class 文件
            return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
        });

        // 循环所有文件
        for (File f : files) {
            // 如果是目录则继续扫描
            if (f.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName+ "." + f.getName(), file.getAbsolutePath(), recursive, classNameList);
            } else {
                // 不是目录，去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                // 添加到集合中
                classNameList.add(packageName + "." + className);
            }
        }
    }






}
