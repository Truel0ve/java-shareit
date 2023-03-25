package ru.practicum.shareit.utility;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;

@UtilityClass
@Slf4j
public class RequestLogger {
    public void logRequest(RequestMethod requestMethod, String path) {
        log.info("A request {} was received at: {}", requestMethod, path);
    }
}