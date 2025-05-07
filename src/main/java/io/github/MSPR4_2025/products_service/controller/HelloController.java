package io.github.MSPR4_2025.products_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello", description = "Example controller that says hello")
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Operation(
            summary = "Say Hello",
            tags = { "hello" })
    @ApiResponse(responseCode = "200", content = { @Content(mediaType = "text/plain") })
    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello!");
    }
}
