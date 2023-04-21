package com.mpepke.inzynierka.demo.service.impl;

import com.mpepke.inzynierka.demo.service.UUIDService;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service

public class UUIDServiceImpl implements UUIDService {
    @Override
    public UUID randomUUID() {
        return UUID.randomUUID();
    }
}
