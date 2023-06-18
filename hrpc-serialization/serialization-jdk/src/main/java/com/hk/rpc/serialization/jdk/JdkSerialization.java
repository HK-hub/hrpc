package com.hk.rpc.serialization.jdk;

import com.hk.rpc.common.exception.SerializeException;
import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author : HK意境
 * @ClassName : JdkSerialization
 * @date : 2023/6/10 14:26
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPIClass
@Slf4j
public class JdkSerialization implements Serialization {

    @Override
    public <T> byte[] serialize(T obj) {

        log.debug("executing jdk serialization...");
        if (obj == null) {
            throw new SerializeException("serialize object is null");
        }

        try{
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(obj);
            return os.toByteArray();
        }catch (IOException e){
            throw new SerializeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        log.debug("executing jdk deserialization...");
        if (data == null){
            throw new SerializeException("deserialize data is null");
        }
        try{
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(is);
            return (T) in.readObject();
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        }

    }
}
