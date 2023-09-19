package com.dnf.helper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Bytes {

    public static <T> int[] intToBytes(T value) {
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
        } else {
            throw new IllegalArgumentException("Unsupported value type");
        }

        return byteArrToIntArr(bytes);
    }

    public static <T> int[] floatToBytes(T value) {
        byte[] bytes;
        if (value instanceof Float) {
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

        return byteArrToIntArr(bytes);
    }

    public static <T> T bytesToInt(int[] value, Class<T> type) {
        byte[] bytes = intArrToByteArr(value);

        // 创建一个ByteBuffer，并设置字节顺序为小端序
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        if (type == Short.class) {
            return type.cast(buffer.getShort());
        } else if (type == Integer.class) {
            return type.cast(buffer.getInt());
        } else if (type == Long.class) {
            return type.cast(buffer.getLong());
        } else {
            throw new IllegalArgumentException("Unsupported data type");
        }
    }


    public static <T> T bytesToFloat(int[] value, Class<T> type) {
        byte[] bytes = intArrToByteArr(value);

        // 创建一个ByteBuffer，并设置字节顺序为小端序
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        if (type == Float.class) {
            return type.cast(buffer.getFloat());
        } else if (type == Double.class) {
            return type.cast(buffer.getDouble());
        } else {
            throw new IllegalArgumentException("Unsupported data type");
        }
    }

    private static byte[] intArrToByteArr(int[] value) {
        byte[] bytes = new byte[value.length];

        for (int i = 0; i < value.length; i++) {
            bytes[i] = (byte) (value[i] & 0xFF);
        }
        return bytes;
    }

    private static int[] byteArrToIntArr(byte[] value) {
        int[] result = new int[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = value[i] & 0xFF;
        }

        return result;
    }

    /**
     * 将多个字节数组连接在一起
     *
     * @param oldArray    原始字节数组
     * @param newArrayArr 要连接的新字节数组
     * @return 连接后的字节数组
     */
    public static int[] addBytes(int[] oldArray, int[]... newArrayArr) {
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
