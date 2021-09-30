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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;


@MicronautTest
public class QuotesControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);

    @Inject
    @Client("/")
    DefaultHttpClient client;

    @Inject
    InMemoryStore store;

    @Test
    void returnsQuotePerSymbol() {
        val appl = initRandomQuote("APPL");
        store.update(appl);

        val amzn = initRandomQuote("AMZN");
        store.update(amzn);

        SoftAssertions.assertSoftly(softly -> {
            val applResult = client.toBlocking().retrieve(GET("/quotes/APPL"), Quote.class);
            LOG.debug("Result: {}", applResult);
            softly.assertThat(appl).usingRecursiveComparison().isEqualTo(applResult);


            val amznResult = client.toBlocking().retrieve(GET("/quotes/AMZN"), Quote.class);
            LOG.debug("Result: {}", amznResult);
            softly.assertThat(amzn).usingRecursiveComparison().isEqualTo(amznResult);
        });
    }

    @Test
    void returnsNotFoundOnUnsupportedSymbol() {
        try {

            client.toBlocking().retrieve(GET("/quotes/UNSUPPORTED"),
                    Argument.of(Quote.class),
                    Argument.of(CustomError.class));

        } catch (HttpClientResponseException e) {

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat((CharSequence) e.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

                LOG.debug("Body: {}", e.getResponse().getBody(CustomError.class));
                val customError = e.getResponse().getBody(CustomError.class);

                softly.assertThat(customError).isPresent();
                softly.assertThat(customError.get().getStatus()).isEqualTo(404);
                softly.assertThat(customError.get().getError()).isEqualTo("NOT_FOUND");
                softly.assertThat(customError.get().getMessage()).isEqualTo("Quote for symbol not found");
                softly.assertThat(customError.get().getPath()).isEqualTo("/quotes/UNSUPPORTED");
            });
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
