package com.learning.udemy.broker.account;

import com.learning.udemy.broker.model.WatchList;
import com.learning.udemy.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Controller("/account/watchlist")
public class WatchListController {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerReactive.class);
    private final InMemoryAccountStore store;
    static final UUID ACCOUNT_ID = UUID.randomUUID();

    public WatchListController(final InMemoryAccountStore store) {
        this.store = store;
    }


    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
        LOG.debug("get - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchlist(ACCOUNT_ID, watchList);
    }

    @Delete(
            value = "/{accountId}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }
}
