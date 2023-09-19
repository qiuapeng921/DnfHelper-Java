package com.dnf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@SpringBootTest
public class BufferTest {
    public static <T> int[] convertToBytes(T value) {
        byte[] bytes;
        if (value instanceof Short) {
            ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt((Short) value);
            bytes = buffer.array();
        } else if (value instanceof Integer) {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt((Integer) value);
            bytes = buffer.array();
        } else if (value instanceof Long) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putLong((Long) value);
            bytes = buffer.array();
        } else if (value instanceof Float) {
            ByteBuffer buffer = ByteBuffer.allocate(Float.BYTES);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putFloat((Float) value);
            bytes = buffer.array();
        } else if (value instanceof Double) {
            ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putDouble((Double) value);
            bytes = buffer.array();
        } else {
            throw new IllegalArgumentException("Unsupported value type");
        }

        int[] result = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i] & 0xFF;
        }

        return result;
    }

    @Test
    public void intToByte() {
        int value = 123; // 要转换的int值

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 设置字节顺序为小端序
        // 将int值转换为byte数组
        byte[] bytes = buffer.putInt(value).array();
        System.out.println(Arrays.toString(bytes));
    }


    @Test
    public void byteToInt() {
        byte[] bytes = {123, 0, 0, 0}; // 要转换的byte数组

        // 创建一个ByteBuffer，并设置字节顺序为小端序
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // 将byte数组转换为int值
        int value = buffer.getInt();
        System.out.println(value); // 输出: 123
    }
}
