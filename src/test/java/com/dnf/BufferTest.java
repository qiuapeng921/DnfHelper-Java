package com.dnf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@SpringBootTest
public class BufferTest {
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
