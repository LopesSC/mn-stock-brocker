package com.learning.udemy.broker.account;

import com.learning.udemy.broker.model.Symbol;
import com.learning.udemy.broker.model.WatchList;
import com.learning.udemy.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.netty.DefaultHttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.DELETE;
import static io.micronaut.http.HttpRequest.PUT;
import static org.junit.jupiter.api.Assertions.*;


@MicronautTest
public class WatchListControllerTest {

    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist")
    DefaultHttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnsEmptyWatchListForAccount() {
        val result = client.toBlocking().retrieve("/", WatchList.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.getSymbols()).isEmpty();
            softly.assertThat(store.getWatchList(TEST_ACCOUNT_ID).getSymbols()).isEmpty();
        });
    }

    @Test
    void returnsWatchListForAccount() {
        val symbols = Stream.of("APPL", "AMZN", "FB", "GOOGLE")
                .map(Symbol::new)
                .collect(Collectors.toList());

        val watchList = new WatchList(symbols);
        store.updateWatchlist(TEST_ACCOUNT_ID, watchList);

        val result = client.toBlocking().retrieve("/", WatchList.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(result.getSymbols().size()).isEqualTo(4);
            softly.assertThat(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size()).isEqualTo(4);
        });
    }

    @Test
    void canUpdateWatchListForAccount() {
        val symbols = Stream.of("APPL", "AMZN", "FB", "GOOGLE")
                .map(Symbol::new)
                .collect(Collectors.toList());

        val watchList = new WatchList(symbols);
        val response = client.toBlocking().exchange(PUT("/", watchList));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat((CharSequence) response.getStatus()).isEqualTo(HttpStatus.OK);
            softly.assertThat(store.getWatchList(TEST_ACCOUNT_ID)).isEqualTo(watchList);
        });
    }

    @Test
    void canDeleteWatchListForAccount() {
        val symbols = Stream.of("APPL", "AMZN", "FB", "GOOGLE")
                .map(Symbol::new)
                .collect(Collectors.toList());

        val watchList = new WatchList(symbols);
        store.updateWatchlist(TEST_ACCOUNT_ID, watchList);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(store.getWatchList(TEST_ACCOUNT_ID).getSymbols()).isNotEmpty();

            val response = client.toBlocking().exchange(DELETE("/" + TEST_ACCOUNT_ID));

            softly.assertThat((CharSequence) response.getStatus()).isEqualTo(HttpStatus.OK);
            softly.assertThat(store.getWatchList(TEST_ACCOUNT_ID).getSymbols()).isEmpty();
        });
    }
}
