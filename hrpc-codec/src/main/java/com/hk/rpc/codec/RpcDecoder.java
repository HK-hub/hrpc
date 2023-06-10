package com.hk.rpc.codec;

import com.hk.rpc.common.utils.SerializationUtil;
import com.hk.rpc.constants.RpcConstants;
import com.hk.rpc.protocol.RpcProtocol;
import com.hk.rpc.protocol.enumeration.RpcType;
import com.hk.rpc.protocol.header.RpcHeader;
import com.hk.rpc.protocol.request.RpcRequest;
import com.hk.rpc.protocol.response.RpcResponse;
import com.hk.rpc.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author : HK意境
 * @ClassName : RpcDecoder
 * @date : 2023/6/10 15:31
 * @description :
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec{


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {

        if (byteBuf.readableBytes() < RpcConstants.HEADER_TOTAL_LENGTH) {
            return;
        }

        byteBuf.markReaderIndex();

        // 读取魔数
        short magic = byteBuf.readShort();
        if (magic != RpcConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal:" + magic);
        }

        // 读取消息类型，状态，请求id
        byte msgType = byteBuf.readByte();
        byte status = byteBuf.readByte();
        long requestId = byteBuf.readLong();

        // 读取序列化方式
        ByteBuf serializeBuf = byteBuf.readBytes(SerializationUtil.MAX_SERIALIZATION_TYPE_COUNT);
        String serializationType = SerializationUtil.removePaddingString(serializeBuf.toString(StandardCharsets.UTF_8));

        // 读取消息体长度
        int bodyLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < bodyLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[bodyLength];
        byteBuf.readBytes(data);

        // 获取消息类型
        RpcType rpcType = RpcType.findByType(msgType);
        if (rpcType == null) {
            return;
        }

        // 组装消息
        // 1.设置消息头
        RpcHeader header = new RpcHeader();
        header.setMagic(magic)
                .setStatus(status)
                .setRequestId(requestId)
                .setMsgType(msgType)
                .setSerializationType(serializationType)
                .setMsgLength(bodyLength);
        // 2.反序列化出消息体
        Serialization serialization = this.getJdkSerialization();
        switch (rpcType) {
            case REQUEST:
                RpcRequest rpcRequest = serialization.deserialize(data, RpcRequest.class);
                if (Objects.nonNull(rpcRequest)) {
                    RpcProtocol<RpcRequest> rpcProtocol = new RpcProtocol<>();
                    rpcProtocol.setHeader(header)
                            .setBody(rpcRequest);
                    out.add(rpcProtocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
            break;
            case HEARTBEAT:
                // TODO 心跳消息
                break;
        }


    }
}
