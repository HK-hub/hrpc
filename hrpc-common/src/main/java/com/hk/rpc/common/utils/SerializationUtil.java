package com.hk.rpc.common.utils;

/**
 * @author : HK意境
 * @ClassName : SerializationUtil
 * @date : 2023/6/10 12:16
 * @description : 主要对消息头中的序列化类型进行处理
 * @Todo :
 * @Bug :
 * @Modified :
 * @Version : 1.0
 */
public class SerializationUtil {

    /**
     * 空白填充字符
     */
    public static final String PADDING_STRING = "0";

    /**
     * 约定序列化类型最大长度
     */
    public static final int MAX_SERIALIZATION_TYPE_COUNT = 16;

    /**
     * 补全序列化类型的空白填充
     * 为长度不足 16 的字符串后面填充0
     * @param str
     * @return 补0后的字符串
     */
    public static String paddingString(String str) {

        str = transNullToEmpty(str);
        if (str.length() >= MAX_SERIALIZATION_TYPE_COUNT) {
            return str;
        }

        // 计算需要填充长度
        int paddingLength = MAX_SERIALIZATION_TYPE_COUNT - str.length();

        // 进行对其填充
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < paddingLength; i++) {
            sb.append(PADDING_STRING);
        }

        return sb.toString();
    }


    /**
     * 去掉字符串中的填充字符
     * @param str
     * @return
     */
    public static String removePaddingString(String str) {

        str = transNullToEmpty(str);
        return str.replace(PADDING_STRING, "");
    }


    /**
     * 转换空字符串为 ”“
     * @param str
     * @return
     */
    private static String transNullToEmpty(String str) {
        return str == null ? "" : str;
    }

}
