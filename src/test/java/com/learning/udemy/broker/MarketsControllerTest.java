package com.learning.udemy.broker;

import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.netty.DefaultHttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

@MicronautTest
public class MarketsControllerTest {

    @Inject
    @Client("/")
    DefaultHttpClient client;

    @Test
    void returnsListOfMarkets() {
        final List<LinkedHashMap<String, String>> result = client.toBlocking().retrieve("/markets", List.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.size()).isEqualTo(7);
            softly.assertThat(result)
                    .extracting(entry -> entry.get("value"))
                    .containsExactlyInAnyOrder("APPL", "AMZN", "FB", "GOOGLE", "MCSFT", "NFLX", "TSLA");
        });
    }
}
