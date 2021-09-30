package com.learning.udemy.broker;

import com.learning.udemy.broker.error.CustomError;
import com.learning.udemy.broker.model.Quote;
import com.learning.udemy.broker.model.Symbol;
import com.learning.udemy.broker.store.InMemoryStore;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.netty.DefaultHttpClient;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@MicronautTest
public class QuotesControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);


    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    DefaultHttpClient client;

    @Inject
    InMemoryStore store;

    @Test
    void returnsQuotePerSymbol() {
        final Quote appl = initRandomQuote("APPL");
        store.update(appl);

        final Quote amzn = initRandomQuote("AMZN");
        store.update(amzn);

        final Quote applResult = client.toBlocking().retrieve(GET("/quotes/APPL"), Quote.class);
        LOG.debug("Result: {}", applResult);
        assertThat(appl).isEqualToComparingFieldByField(applResult);


        final Quote amznResult = client.toBlocking().retrieve(GET("/quotes/AMZN"), Quote.class);
        LOG.debug("Result: {}", amznResult);
        assertThat(amzn).isEqualToComparingFieldByField(amznResult);

    }

    @Test
    void returnsNotFoundOnUnsupportedSymbol() {
        try {

            client.toBlocking().retrieve(GET("/quotes/UNSUPPORTED"),
                    Argument.of(Quote.class),
                    Argument.of(CustomError.class));

        } catch (HttpClientResponseException e) {

            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
            LOG.debug("Body: {}", e.getResponse().getBody(CustomError.class));
            final Optional<CustomError> customError = e.getResponse().getBody(CustomError.class);

            assertTrue(customError.isPresent());
            assertEquals(404, customError.get().getStatus());
            assertEquals("NOT_FOUND", customError.get().getError());
            assertEquals("Quote for symbol not found", customError.get().getMessage());
            assertEquals("/quotes/UNSUPPORTED", customError.get().getPath());

        }
    }

    private Quote initRandomQuote(String symbolValue) {
        return Quote.builder().symbol(new Symbol(symbolValue))
                .bid(randomValue())
                .ask(randomValue())
                .lastPrice(randomValue())
                .volume(randomValue())
                .build();
    }

    private BigDecimal randomValue() {

        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }


}
