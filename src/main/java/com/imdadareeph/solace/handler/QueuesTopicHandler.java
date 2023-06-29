package com.imdadareeph.solace.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class QueuesTopicHandler {

    public boolean created;
}
