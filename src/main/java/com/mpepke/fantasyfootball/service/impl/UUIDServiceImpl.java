package com.mpepke.fantasyfootball.service.impl;

import com.mpepke.fantasyfootball.service.UUIDService;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service

public class UUIDServiceImpl implements UUIDService {
    @Override
    public UUID randomUUID() {
        return UUID.randomUUID();
    }
}
