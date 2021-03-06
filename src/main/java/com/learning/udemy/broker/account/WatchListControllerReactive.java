package com.learning.udemy.broker.account;

import com.learning.udemy.broker.model.WatchList;
import com.learning.udemy.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.Executor;

@Controller("/account/watchlist-reactive")
public class WatchListControllerReactive {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerReactive.class);
    static final UUID ACCOUNT_ID = UUID.randomUUID();

    private final InMemoryAccountStore store;
    private final Scheduler scheduler;


    public WatchListControllerReactive(final InMemoryAccountStore store, @Named(TaskExecutors.IO) Executor executerService) {
        this.store = store;
        this.scheduler = Schedulers.from(executerService);
    }


    @Get(produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public WatchList get() {
        LOG.debug("getWatchlist - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Get(
            value = "/single",
            produces = MediaType.APPLICATION_JSON
    )
    public Flowable<WatchList> getAsSingle() {
        return Single.fromCallable(() -> {
            LOG.debug("getAsSingle - {}", Thread.currentThread().getName());
            return store.getWatchList(ACCOUNT_ID);
        }).toFlowable().subscribeOn(scheduler);
    }

    @Put(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    @ExecuteOn(TaskExecutors.IO)
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchlist(ACCOUNT_ID, watchList);
    }

    @Delete(
            value = "/{accountId}",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    @ExecuteOn(TaskExecutors.IO)
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }
}
