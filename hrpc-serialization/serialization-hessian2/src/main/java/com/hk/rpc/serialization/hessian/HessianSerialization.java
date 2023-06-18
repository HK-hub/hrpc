package com.hk.rpc.serialization.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hk.rpc.common.exception.SerializeException;
import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author : HK意境
 * @ClassName : HessianSerialization
 * @date : 2023/6/10 14:26
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
@SPIClass
@Slf4j
public class HessianSerialization implements Serialization {

    @Override
    public <T> byte[] serialize(T obj) {

        log.debug("executing hessian2 serialization...");
        if (obj == null) {
            throw new SerializeException("serialize object is null");
        }

        // 序列化
        byte[] bytes = new byte[0];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);

        try{
            hessian2Output.startMessage();
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            hessian2Output.completeMessage();
            bytes = byteArrayOutputStream.toByteArray();

        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        } finally {
            // 关闭流
            try{
                hessian2Output.close();
                byteArrayOutputStream.close();
            }catch(Exception e){
                log.warn("fail to close hessian2 output and byteArrayOutputStream:", e);
            }
        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        log.debug("executing hessian2 deserialization...");
        if (data == null){
            throw new SerializeException("deserialize data is null");
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Hessian2Input hessian2Input = new Hessian2Input(byteArrayInputStream);

        // 反序列化
        T obj = null;
        try{
            hessian2Input.startMessage();
            obj = (T) hessian2Input.readObject();
            hessian2Input.completeMessage();
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        } finally {
            // 关闭流
            try{
                hessian2Input.close();
                byteArrayInputStream.close();
            }catch(Exception e){
                log.warn("fail to close hessian2 input and byteArrayInputStream:", e);
            }
        }
        return obj;
    }
}
