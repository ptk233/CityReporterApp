package com.example.cityreporter.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TestController {

    @GetMapping("/test")
    fun test(): Map<String, String> {
        return mapOf(
            "message" to "City Reporter API is working!",
            "status" to "OK",
            "timestamp" to System.currentTimeMillis().toString()
        )
    }

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf("status" to "UP")
    }
}