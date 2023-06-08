package com.hk.rpc.common.scanner;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        File dir = new File(packagePath);

        // 不存在，不是目录直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        // 如果存在就获取包下的所有文件
        File[] fileDirs = dir.listFiles(file -> {
            // 自定义过滤规则：如果可以循环或者是 class 文件
            return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
        });

        // 循环所有文件
        for (File file : fileDirs) {
            // 如果是目录则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classNameList);
            } else {
                // 不是目录，去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                // 添加到集合中
                classNameList.add(packageName + "." + className);
            }
        }
    }

    /**
     * 扫描Jar 文件中指定包下的所有类信息
     * @param packageName 扫描的包名
     * @param recursive 是否递归调用
     * @param packageDirName    当前包名的前面部分的名称
     * @param url   包的url地址
     * @param classNameList 完成类名存放集合
     * @return String 处理后的包名，以供下次使用
     */
    private static String findAndAddClassesInPackageByJar(String packageName, boolean recursive, String packageDirName,
                                                          URL url, List<String> classNameList) throws IOException {

        // 如果是 jar 包，
        JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();

        // 从此 jar 包得到一个枚举类
        Enumeration<JarEntry> entries = jarFile.entries();
        // 迭代处理
        while (entries.hasMoreElements()) {
            // 获取jar 里的一个实体，可以是目录和一些其他文件，例如META-INF等文件
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            // 如果是以 / 开头
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }

            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                // 如果以 / 结尾是一个包
                if (idx != -1) {
                    // 获取报名，替换 / 为 .
                    packageName = name.substring(0, idx).replace('/', '.');
                }

                // 如果是一个包，并且可以迭代
                if (idx != -1 && recursive) {
                    // 如果是 .class 文件
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !entry.isDirectory()) {
                        // 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        classNameList.add(packageName + "." + className);
                    }
                }
            }
        }
        return packageName;
    }


    /**
     * 扫描指定包下的所有类信息
     * @param packageName 指定的包名
     * @return List<String> 指定包下所有完整类名的集合
     */
    public static List<String> getClassNameList(String packageName) throws IOException {

        ArrayList<String> classNameList = new ArrayList<>();
        boolean recursive = true;
        // 获取包的名字并进行替换
        String packageDirName = packageName.replace('.', '/');

        // 定义一个枚举集合，并进行循环来处理目录下的东西
        Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (dirs.hasMoreElements()) {
            // 获取下一个元素
            URL url = dirs.nextElement();
            // 得到协议名称
            String protocol = url.getProtocol();
            // 如果是以文件的形式保存
            if (PROTOCOL_FILE.equals(protocol)) {
                // 获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的方式扫描整个包下的文件，并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNameList);
            } else if (PROTOCOL_JAR.equals(protocol)) {

                // jar 包文件存在
                packageName = findAndAddClassesInPackageByJar(packageName, recursive, packageDirName, url, classNameList);
            }
        }

        return classNameList;
    }



}
