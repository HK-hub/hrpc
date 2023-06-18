package com.hk.rpc.serialization.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.hk.rpc.common.exception.SerializeException;
import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author : HK意境
 * @ClassName : KryoSerialization
 * @date : 2023/6/10 14:26
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPIClass
@Slf4j
public class KryoSerialization implements Serialization {

    @Override
    public <T> byte[] serialize(T obj) {

        log.debug("executing kryo serialization...");
        if (obj == null) {
            throw new SerializeException("serialize object is null");
        }

        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(obj.getClass(), new JavaSerializer());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);

        kryo.writeClassAndObject(output, obj);
        output.flush();
        output.close();

        byte[] bytes = baos.toByteArray();
        // 序列化
        try{
            baos.flush();
            baos.close();
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        }

        return bytes;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        log.debug("executing kryo deserialization...");
        if (data == null){
            throw new SerializeException("deserialize data is null");
        }

        // 反序列化
        T obj = null;
        try{
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(clazz, new JavaSerializer());

            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            Input input = new Input(bais);
            obj = (T) kryo.readClassAndObject(input);
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        }

        return obj;
    }
}
