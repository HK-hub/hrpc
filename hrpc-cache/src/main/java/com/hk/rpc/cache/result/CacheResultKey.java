package com.hk.rpc.cache.result;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author : HK意境
 * @ClassName : CacheResultKey
 * @date : 2023/7/14 17:01
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class CacheResultKey implements Serializable {

    private static final long serialVersionUID = -2718946818925001039L;


    /**
     * 保存缓存时的时间戳
     */
    @Getter
    @Setter
    private long cacheTimestamp;


    /**
     * 服务类名
     */
    private String className;


    /**
     * 方法名称
     */
    private String methodName;


    /**
     * 参数类型集合
     */
    private Class<?>[] parameterTypes;


    /**
     * 参数集合
     */
    private Object[] parameters;


    /**
     * 服务版本
     */
    private String version;


    /**
     * 服务分组
     */
    private String group;


    public CacheResultKey(String className, String methodName, Class<?>[] parameterTypes, Object[] parameters, String version, String group) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.version = version;
        this.group = group;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheResultKey that = (CacheResultKey) o;
        return Objects.equals(className, that.className) &&
                Objects.equals(methodName, that.methodName) &&
                Arrays.equals(parameterTypes, that.parameterTypes) &&
                Arrays.equals(parameters, that.parameters) &&
                Objects.equals(version, that.version) &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(className, methodName, version, group);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

}
