package com.hk.rpc.serialization.fst;


import com.hk.rpc.common.exception.SerializeException;
import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import org.nustaq.serialization.FSTConfiguration;

/**
 * @author : HK意境
 * @ClassName : FstSerialization
 * @date : 2023/6/10 14:26
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPIClass
@Slf4j
public class FstSerialization implements Serialization {

    @Override
    public <T> byte[] serialize(T obj) {

        log.debug("executing fst serialization...");
        if (obj == null) {
            throw new SerializeException("serialize object is null");
        }

        // 序列化
        try{
            FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
            return configuration.asByteArray(obj);
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        log.debug("executing fst deserialization...");
        if (data == null){
            throw new SerializeException("deserialize data is null");
        }

        // 反序列化
        T obj = null;
        try{
            FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
            obj = (T) configuration.asObject(data);
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        }
        return obj;
    }
}
