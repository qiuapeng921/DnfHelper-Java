package com.dnf.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendPack {
    Logger logger = LoggerFactory.getLogger(SendPack.class.getName());
    private List<Integer> data;


    private void hcCall(int param) {
        data.add(1);
    }


    private void jmCall(int param) {
        data.add(param);
    }

    private void fbCall() {
        data.clear();
    }
}
