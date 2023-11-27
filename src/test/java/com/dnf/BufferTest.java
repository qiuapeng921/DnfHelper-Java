package com.dnf;

import com.dnf.driver.ltq.ReadWrite;
import com.sun.jna.Memory;
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
    public void test() {
        // 创建一个指定大小的空字节集
        Memory buffer = new Memory(1);

        // 准备输入缓冲区
        ReadWrite inputData = new ReadWrite();
        inputData.setProcessId(1);
        inputData.setData(buffer);
        inputData.setMemoryAddress(0x100000L);
        inputData.setSize(1);
        inputData.setKey("");

        System.out.println("inputData = " + inputData);
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
        int[] intArr = {123, 0, 0, 0};

        byte[] bytes = new byte[intArr.length];

        for (int i = 0; i < intArr.length; i++) {
            bytes[i] = (byte) (intArr[i] & 0xFF);
        }

        // 创建一个ByteBuffer，并设置字节顺序为小端序
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // 将byte数组转换为int值
        int value = buffer.getInt();
        System.out.println(value); // 输出: 123
    }
}
