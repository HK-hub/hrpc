package com.hk.rpc.reflect.asm.visitor;


import com.hk.rpc.reflect.asm.proxy.ReflectProxy;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName : TargetClassVisitor
 * @author : HK意境
 * @date : 2023/6/19 15:51
 * @description : 用于访问代理目标类的信息
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class TargetClassVisitor extends ClassVisitor {

    private boolean isFinal;
    private List<MethodBean> methods = new ArrayList<>();
    private List<MethodBean> declaredMethods = new ArrayList<>();
    private List<MethodBean> constructors = new ArrayList<>();

    public TargetClassVisitor() {
        super(ReflectProxy.ASM_VERSION);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL){
            isFinal = true;
        }
        if (superName != null) {
            List<MethodBean> beans = initMethodBeanByParent(superName);
            if (beans != null && !beans.isEmpty()) {
                for (MethodBean bean : beans) {
                    if (!methods.contains(bean)) {
                        methods.add(bean);
                    }
                }
            }
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ("<init>".equals(name)){
            // 构造方法
            MethodBean constructor = new MethodBean(access, name, descriptor);
            constructors.add(constructor);
        } else if (!"<clinit>".equals(name)) {
            // 其他方法
            if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL
                    || (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
            MethodBean methodBean = new MethodBean(access, name, descriptor);
            declaredMethods.add(methodBean);
            if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
                methods.add(methodBean);
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public List<MethodBean> getMethods() {
        return methods;
    }

    public List<MethodBean> getDeclaredMethods() {
        return declaredMethods;
    }

    public List<MethodBean> getConstructors() {
        return constructors;
    }

    private List<MethodBean> initMethodBeanByParent(String superName){
        try {
            if (superName != null && !superName.isEmpty()){
                ClassReader reader = new ClassReader(superName);
                TargetClassVisitor visitor = new TargetClassVisitor();
                reader.accept(visitor, ClassReader.SKIP_DEBUG);
                List<MethodBean> beans = new ArrayList<>();
                for (MethodBean methodBean : visitor.methods) {
                    // 跳过 final 和 static
                    if ((methodBean.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL
                            || (methodBean.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
                        continue;
                    }
                    // 只要 public
                    if ((methodBean.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
                        beans.add(methodBean);
                    }
                }
                return beans;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Getter
    @Setter
    public static class MethodBean {

        public int access;
        public String methodName;
        public String methodDesc;

        public MethodBean() {
        }

        public MethodBean(int access, String methodName, String methodDesc) {
            this.access = access;
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null){
                return false;
            }
            if (!(obj instanceof MethodBean)){
                return false;
            }
            MethodBean bean = (MethodBean) obj;
            if (access == bean.access
                    && methodName != null
                    && bean.methodName != null
                    && methodName.equals(bean.methodName)
                    && methodDesc != null
                    && bean.methodDesc != null
                    && methodDesc.equals(bean.methodDesc)){
                return true;
            }
            return false;
        }
    }
}
