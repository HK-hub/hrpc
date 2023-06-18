package com.hk.rpc.serialization.protobuf;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.hk.rpc.common.exception.SerializeException;
import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : HK意境
 * @ClassName : ProtobufSerialization
 * @date : 2023/6/18 17:56
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@Slf4j
@SPIClass
public class ProtobufSerialization implements Serialization {

    private Map<Class<?>, Schema<?>> cachedSchemas = new ConcurrentHashMap<>();

    private Objenesis objenesis = new ObjenesisStd(true);



    @SuppressWarnings("unchecked")
    @Override
    public <T> byte[] serialize(T obj) {

        log.debug("executing protobuf serialization...");
        if (Objects.isNull(obj)) {
            throw new SerializeException("serialize object is null");
        }

        Class<T> aClass = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try{
            Schema<T> schema = this.getSchema(aClass);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        }catch(Exception e){
            throw new SerializeException(e.getMessage(), e);
        }finally {
            buffer.clear();
        }
    }


    /**
     * 从缓存中获取Schema 或者 创建
     * @param aClass
     * @param <T>
     * @return
     */
    private <T> Schema<T> getSchema(Class<T> aClass) {

        Schema<T> schema = (Schema<T>) cachedSchemas.get(aClass);
        if (Objects.isNull(schema)) {
            schema = RuntimeSchema.createFrom(aClass);
            if (Objects.nonNull(schema)) {
                cachedSchemas.put(aClass, schema);
            }
        }

        return schema;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        log.debug("executing protobuf deserialization...");
        if (Objects.isNull(data)) {
            throw new SerializeException("deserialize data is null");
        }

        try{
            T obj = objenesis.newInstance(clazz);
            Schema<T> schema = this.getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return obj;
        }catch(Exception e){
            throw new SerializeException(e.getMessage(), e);
        }
    }
}
