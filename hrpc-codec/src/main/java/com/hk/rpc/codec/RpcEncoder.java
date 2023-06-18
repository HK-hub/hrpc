package com.hk.rpc.codec;

import com.hk.rpc.common.utils.SerializationUtil;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author : HK意境
 * @ClassName : RpcEncoder
 * @date : 2023/6/10 15:16
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec{

    /**
     * 编码
     * @param ctx
     * @param message 需要编码的消息
     * @param byteBuf 编码存放位置
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> message, ByteBuf byteBuf) throws Exception {

        RpcHeader header = message.getHeader();

        // 写入消息头
        byteBuf.writeShort(header.getMagic())
                .writeByte(header.getMsgType())
                .writeByte(header.getStatus())
                .writeLong(header.getRequestId());

        // 获取序列化类型
        String serializationType = header.getSerializationType();
        // TODO Serialization 序列化是扩展点
        Serialization jdkSerialization = this.getSerialization(serializationType);

        // 写入序列化类型
        byteBuf.writeCharSequence(SerializationUtil.paddingString(serializationType), StandardCharsets.UTF_8);
        // 序列化消息体
        byte[] data = jdkSerialization.serialize(message.getBody());

        // 写入消息体长度
        byteBuf.writeInt(data.length);

        // 写入消息体
        byteBuf.writeBytes(data);
    }
}
