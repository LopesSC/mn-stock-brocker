package com.learning.udemy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.netty.DefaultHttpClient;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class HelloWorldControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    DefaultHttpClient client;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testHelloResponse() {
        val result = client.toBlocking().retrieve("/hello");
        assertEquals("Hello from service", result);
    }

    @Test
    void returnsGermanGreeting() {
        val result = client.toBlocking().retrieve("/hello/de");
        assertEquals("Hallo", result);
    }

    @Test
    void returnsEnglishGreeting() {
        val result = client.toBlocking().retrieve("/hello/en");
        assertEquals("Hello", result);
    }

    @Test
    void returnsGreetingAsJson() {
        val result = client.toBlocking().retrieve("/hello/json", ObjectNode.class);
        assertTrue(result.toString().contains("{\"myText\":\"Hello World\",\"id\":1,\"timeUTC\":"));
    }
}
