package com.hk.rpc.serialization.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hk.rpc.common.exception.SerializeException;
import com.hk.rpc.serialization.api.Serialization;
import com.hk.rpc.spi.annotation.SPIClass;
import lombok.extern.slf4j.Slf4j;
import java.text.SimpleDateFormat;

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
public class JsonSerialization implements Serialization {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 初始化，注册 Jackson 序列化模块
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OBJECT_MAPPER.setDateFormat(dateFormat);

        // 这里采用默认序列化方式，避免因为业务问题造成框架抛弃序列化数据
        // OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 缩放输出
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

        OBJECT_MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        OBJECT_MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);

        OBJECT_MAPPER.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        OBJECT_MAPPER.disable(SerializationFeature.CLOSE_CLOSEABLE);
        OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        OBJECT_MAPPER.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
    }


    @Override
    public <T> byte[] serialize(T obj) {

        log.debug("executing json serialization...");
        if (obj == null) {
            throw new SerializeException("serialize object is null");
        }

        // 序列化
        byte[] bytes = new byte[0];
        try{
            bytes = OBJECT_MAPPER.writeValueAsBytes(obj);
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        log.debug("executing json deserialization...");
        if (data == null){
            throw new SerializeException("deserialize data is null");
        }

        // 反序列化
        T obj = null;
        try{
            obj = OBJECT_MAPPER.readValue(data, clazz);
        }catch (Exception e){
            throw new SerializeException(e.getMessage(), e);
        }
        return obj;
    }
}
