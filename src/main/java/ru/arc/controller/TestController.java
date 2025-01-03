package ru.arc.controller;

import lombok.RequiredArgsConstructor;
import ru.arc.service.TradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TradeService tradeService;

    @GetMapping("/test")
    public String test(@RequestParam final String coin) {
        return tradeService.retrieveBalance(coin);
    }

    @GetMapping("/sell")
    public void sell(final String message) {
        tradeService.sell(message);
    }
}
