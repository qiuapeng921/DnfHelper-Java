package com.dnf.game;

import com.dnf.helper.Bytes;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendPack {
    Logger logger = LoggerFactory.getLogger(SendPack.class.getName());
    @Resource
    private GameCall gameCall;
    private List<Integer> data;


    private void hcCall(int param) {

    }

    private void jmCall(int param) {
        data.add(param);
    }

    private void fbCall() {
        data.clear();
    }
}
