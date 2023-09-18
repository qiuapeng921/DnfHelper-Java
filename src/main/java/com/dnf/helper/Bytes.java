package com.dnf.helper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Bytes {

    /**
     * 将整数转换为字节数组
     *
     * @param intVal 整数值
     * @param size   整数类型（2表示short，4表示int，8表示long）
     * @return 字节数组
     */
    public static int[] intToBytes(long intVal, int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        int[] intArray = new int[size];
        for (int i = 0; i < size; i++) {
            intArray[i] = (int) ((intVal >> (8 * i)) & 0xff);
        }
        return intArray;
    }

    /**
     * 将浮点数转换为字节数组
     *
     * @param floatVal  浮点数值
     * @param floatType 浮点数类型（4表示float，8表示double）
     * @return 字节数组
     */
    public static int[] floatToBytes(float floatVal, int floatType) {
        ByteBuffer buffer = ByteBuffer.allocate(floatType);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        if (floatType == 4) {
            buffer.putFloat(floatVal);
        } else if (floatType == 8) {
            buffer.putDouble(floatVal);
        }
        byte[] byteArray = buffer.array();
        int[] intArray = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            intArray[i] = byteArray[i];
        }
        return intArray;
    }

    /**
     * 将多个字节数组连接在一起
     *
     * @param oldArray    原始字节数组
     * @param newArrayArr 要连接的新字节数组
     * @return 连接后的字节数组
     */
    public int[] addBytes(int[] oldArray, int[]... newArrayArr) {
        int totalLength = oldArray.length;
        for (int[] array : newArrayArr) {
            totalLength += array.length;
        }
        int[] result = new int[totalLength];
        System.arraycopy(oldArray, 0, result, 0, oldArray.length);
        int offset = oldArray.length;
        for (int[] array : newArrayArr) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
