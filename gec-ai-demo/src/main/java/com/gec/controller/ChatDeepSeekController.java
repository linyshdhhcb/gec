package com.gec.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatDeepSeekController {

    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "你是谁")
                       String message) {
        //prompt:提示词
        return this.chatClient.prompt()
                //用户输入的信息
                .user(message)
                //请求大模型
                .call()
                //返回文本
                .content();
    }

    @GetMapping(value = "/chatstream", produces = "text/html;charset=UTF-8")
    public Flux<String> chatStream(@RequestParam(value = "message") String message,
                                   @RequestParam(value = "chatId") String chatId) {
        return this.chatClient
                .prompt()
                .user(message)
                .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .stream()
                .content();
    }

}
