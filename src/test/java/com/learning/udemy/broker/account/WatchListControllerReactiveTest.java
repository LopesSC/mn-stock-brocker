package com.learning.udemy.broker.account;

import com.learning.udemy.broker.model.Symbol;
import com.learning.udemy.broker.model.WatchList;
import com.learning.udemy.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.rxjava3.http.client.Rx3HttpClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.*;


@MicronautTest
public class WatchListControllerReactiveTest {

    private static  final UUID TEST_ACCOUNT_ID = WatchListControllerReactive.ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist-reactive")
    Rx3HttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnsEmptyWatchListForAccount() {
        final Single<WatchList> result = client.retrieve(GET("/"), WatchList.class).singleOrError();
        assertTrue(result.blockingGet().getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "FB", "GOOGLE")
                .map(Symbol::new)
                .collect(Collectors.toList());

        WatchList watchList = new WatchList(symbols);
        store.updateWatchlist(TEST_ACCOUNT_ID, watchList);

        final WatchList result = client.retrieve(GET("/"), WatchList.class).blockingFirst();
        assertEquals(4, result.getSymbols().size());
        assertEquals(4, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void returnsWatchListForAccountAsSingle() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "FB", "GOOGLE")
                .map(Symbol::new)
                .collect(Collectors.toList());

        WatchList watchList = new WatchList(symbols);
        store.updateWatchlist(TEST_ACCOUNT_ID, watchList);

        final WatchList result = client.retrieve(GET("/single"), WatchList.class).blockingFirst();
        assertEquals(4, result.getSymbols().size());
        assertEquals(4, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }


    @Test
    void canUpdateWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "FB", "GOOGLE")
                .map(Symbol::new)
                .collect(Collectors.toList());

        WatchList watchList = new WatchList(symbols);

        val response = client.exchange(PUT("/", watchList)).blockingFirst();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    void canDeleteWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "FB", "GOOGLE")
                .map(Symbol::new)
                .collect(Collectors.toList());

        WatchList watchList = new WatchList(symbols);
        store.updateWatchlist(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

        val response = client.exchange(DELETE("/" + TEST_ACCOUNT_ID)).blockingFirst();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

    }

}
