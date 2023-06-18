package com.hk.rpc.proxy.asm.factory;


import com.hk.rpc.proxy.asm.proxy.ASMProxy;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author : HK意境
 * @ClassName : ASMGenerateProxyFoactory
 * @date : 2023/6/18 20:08
 * @description : 生成ASM动态代理对象工厂类
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class ASMGenerateProxyFoactory {


    private static final Integer DEFAULT_NUM = 1;

    public static byte[] generateClass(Class<?>[] interfaces, String proxyClassName) {
        //创建一个ClassWriter对象，自动计算栈帧和局部变量表大小
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        //创建的java版本、访问标志、类名、父类、接口
        String internalName = Type.getInternalName(ASMProxy.class);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, proxyClassName, null, internalName, getInterfacesName(interfaces));
        //创建<init>
        createInit(cw);
        //创建static
        addStatic(cw, interfaces);
        //创建<clinit>
        addClinit(cw, interfaces, proxyClassName);
        //实现接口方法
        addInterfacesImpl(cw, interfaces, proxyClassName);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private static String[] getInterfacesName(Class<?>[] interfaces) {
        String[] interfacesName = new String[interfaces.length];
        return Arrays.stream(interfaces).map(Type::getInternalName).collect(Collectors.toList()).toArray(interfacesName);
    }

    /**
     * 创建init方法
     * 调用父类的构造方法
     * 0 aload_0
     * 1 aload_1
     * 2 invokespecial #1 <proxy/ASMProxy.<init> : (Ljava/lang/reflect/InvocationHandler;)V>
     * 5 return
     * @param cw
     */
    private static void createInit(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/reflect/InvocationHandler;)V", null, null);
        mv.visitCode();
        //将this入栈
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        //将参数入栈
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        //调用父类初始化方法
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(ASMProxy.class), "<init>", "(Ljava/lang/reflect/InvocationHandler;)V", false);
        // 返回
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    /**
     * 创建static字段
     * @param cw
     * @param interfaces
     */
    private static void addStatic(ClassWriter cw, Class<?>[] interfaces) {
        for (Class<?> anInterface : interfaces) {
            for (int i = 0; i < anInterface.getMethods().length; i++) {
                String methodName = "_" + anInterface.getSimpleName() + "_" + i;
                cw.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, methodName, Type.getDescriptor(Method.class), null, null);
            }
        }
    }

    private static void addClinit(ClassWriter cw, Class<?>[] interfaces, String proxyClassName) {
        //_UserService_0 = Class.forName("proxy.UserService").getMethod("login", String.class, String.class);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();
        for (Class<?> anInterface : interfaces) {
            for (int i = 0; i < anInterface.getMethods().length; i++) {
                Method method = anInterface.getMethods()[i];
                String methodName = "_" + anInterface.getSimpleName() + "_" + i;
                mv.visitLdcInsn(anInterface.getName());
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
                mv.visitLdcInsn(method.getName());
                if (method.getParameterCount() == 0) {
                    mv.visitInsn(Opcodes.ACONST_NULL);
                } else {
                    switch (method.getParameterCount()) {
                        case 1:
                            mv.visitInsn(Opcodes.ICONST_1);
                            break;
                        case 2:
                            mv.visitInsn(Opcodes.ICONST_2);
                            break;
                        case 3:
                            mv.visitInsn(Opcodes.ICONST_3);
                            break;
                        default:
                            mv.visitVarInsn(Opcodes.BIPUSH, method.getParameterCount());
                            break;
                    }
                    mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Class.class));
                    for (int paramIndex = 0; paramIndex < method.getParameterTypes().length; paramIndex++) {
                        Class<?> parameter = method.getParameterTypes()[paramIndex];
                        mv.visitInsn(Opcodes.DUP);
                        switch (paramIndex) {
                            case 0:
                                mv.visitInsn(Opcodes.ICONST_0);
                                break;
                            case 1:
                                mv.visitInsn(Opcodes.ICONST_1);
                                break;
                            case 2:
                                mv.visitInsn(Opcodes.ICONST_2);
                                break;
                            case 3:
                                mv.visitInsn(Opcodes.ICONST_3);
                                break;
                            default:
                                mv.visitVarInsn(Opcodes.BIPUSH, paramIndex);
                                break;
                        }
                        mv.visitLdcInsn(parameter.getName());
                        mv.visitMethodInsn(
                                Opcodes.INVOKESTATIC, Type.getInternalName(Class.class),
                                "forName",
                                "(Ljava/lang/String;)Ljava/lang/Class;",
                                false
                        );
                        mv.visitInsn(Opcodes.AASTORE);
                    }
                }
//                invokevirtual #13 <java/lang/Class.getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;>
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Class.class), "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
                //putstatic #3 <proxy/$Proxy0._UserService_0 : Ljava/lang/reflect/Method;>
                mv.visitFieldInsn(Opcodes.PUTSTATIC, proxyClassName, methodName, Type.getDescriptor(Method.class));
            }
            mv.visitInsn(Opcodes.RETURN);
        }
        mv.visitMaxs(DEFAULT_NUM, DEFAULT_NUM);
        mv.visitEnd();
    }

    private static void addInterfacesImpl(ClassWriter cw, Class<?>[] interfaces, String proxyClassName) {
        for (Class<?> anInterface : interfaces) {
            for (int i = 0; i < anInterface.getMethods().length; i++) {
                Method method = anInterface.getMethods()[i];
                String methodName = "_" + anInterface.getSimpleName() + "_" + i;
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, new String[]{Type.getInternalName(Exception.class)});
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(ASMProxy.class), "h", "Ljava/lang/reflect/InvocationHandler;");
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETSTATIC, proxyClassName, methodName, Type.getDescriptor(Method.class));
                switch (method.getParameterCount()) {
                    case 0:
                        mv.visitInsn(Opcodes.ICONST_0);
                        break;
                    case 1:
                        mv.visitInsn(Opcodes.ICONST_1);
                        break;
                    case 2:
                        mv.visitInsn(Opcodes.ICONST_2);
                        break;
                    case 3:
                        mv.visitInsn(Opcodes.ICONST_3);
                        break;
                    default:
                        mv.visitVarInsn(Opcodes.BIPUSH, method.getParameterCount());
                        break;
                }
                mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getInternalName(Object.class));
                //     * 12 dup
                //     * 13 iconst_0
                //     * 14 aload_1
                //     * 15 aastore
                for (int paramIndex = 0; paramIndex < method.getParameterCount(); paramIndex++) {
                    mv.visitInsn(Opcodes.DUP);
                    switch (paramIndex) {
                        case 0:
                            mv.visitInsn(Opcodes.ICONST_0);
                            break;
                        case 1:
                            mv.visitInsn(Opcodes.ICONST_1);
                            break;
                        case 2:
                            mv.visitInsn(Opcodes.ICONST_2);
                            break;
                        case 3:
                            mv.visitInsn(Opcodes.ICONST_3);
                            break;
                        default:
                            mv.visitVarInsn(Opcodes.BIPUSH, paramIndex);
                            break;
                    }
                    mv.visitVarInsn(Opcodes.ALOAD, paramIndex + 1);
                    mv.visitInsn(Opcodes.AASTORE);
                }
// * 20 invokeinterface #5 <java/lang/reflect/InvocationHandler.invoke : (Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;> count 4
//     * 25 checkcast #6 <java/lang/Boolean>
//     * 28 invokevirtual #7 <java/lang/Boolean.booleanValue : ()Z>
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type.getInternalName(InvocationHandler.class), "invoke",
                        "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                addReturn(mv, method.getReturnType());
//                mv.visitFrame(Opcodes.F_FULL, 0, null, 0, null);
                mv.visitMaxs(DEFAULT_NUM, DEFAULT_NUM);
                mv.visitEnd();
            }
        }
    }

    //添加方法返回
    private static void addReturn(MethodVisitor mv, Class<?> returnType) {
        if (returnType.isAssignableFrom(Void.class)) {
            mv.visitInsn(Opcodes.RETURN);
            return;
        }
        if (returnType.isAssignableFrom(boolean.class)) {
            //checkcast #6 <java/lang/Boolean>
            //     * 28 invokevirtual #7 <java/lang/Boolean.booleanValue : ()Z>
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Boolean.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Boolean.class), "booleanValue", "()Z", false);
            mv.visitInsn(Opcodes.IRETURN);
        } else if (returnType.isAssignableFrom(int.class)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Integer.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Integer.class), "intValue", "()I", false);
            mv.visitInsn(Opcodes.IRETURN);
        } else if (returnType.isAssignableFrom(long.class)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Long.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Long.class), "longValue", "()J", false);
            mv.visitInsn(Opcodes.LRETURN);
        } else if (returnType.isAssignableFrom(short.class)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Short.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Short.class), "shortValue", "()S", false);
            mv.visitInsn(Opcodes.IRETURN);
        } else if (returnType.isAssignableFrom(byte.class)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Byte.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Byte.class), "byteValue", "()B", false);
            mv.visitInsn(Opcodes.IRETURN);
        } else if (returnType.isAssignableFrom(char.class)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Character.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Character.class), "charValue", "()C", false);
            mv.visitInsn(Opcodes.IRETURN);
        } else if (returnType.isAssignableFrom(float.class)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Float.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Float.class), "floatValue", "()F", false);
            mv.visitInsn(Opcodes.FRETURN);
        } else if (returnType.isAssignableFrom(double.class)) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Double.class));
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Double.class), "doubleValue", "()D", false);
            mv.visitInsn(Opcodes.DRETURN);
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(returnType));
            mv.visitInsn(Opcodes.ARETURN);
        }
    }

}
