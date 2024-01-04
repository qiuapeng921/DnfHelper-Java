package com.dnf.driver.ltq;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 情歌
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReadWrite extends Structure {
    public long processId;
    public long memoryAddress;
    public long size;
    public Pointer data;
    public String key;
}