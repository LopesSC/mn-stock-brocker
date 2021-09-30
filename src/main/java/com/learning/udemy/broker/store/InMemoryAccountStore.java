package com.learning.udemy.broker.store;

import com.learning.udemy.broker.model.WatchList;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {

    private final HashMap<UUID, WatchList> watchListsPerAccount = new HashMap<>();

    public WatchList getWatchList(UUID accountId) {
        return watchListsPerAccount.getOrDefault(accountId, new WatchList());
    }

    public WatchList updateWatchlist(UUID accountId, WatchList watchList) {
        watchListsPerAccount.put(accountId, watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(UUID accountId) {
        watchListsPerAccount.remove(accountId);
    }
}
