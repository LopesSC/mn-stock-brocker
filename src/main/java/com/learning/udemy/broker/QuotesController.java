package com.learning.udemy.broker;

import com.learning.udemy.broker.error.CustomError;
import com.learning.udemy.broker.model.Quote;
import com.learning.udemy.broker.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import java.util.Optional;

@Controller("/quotes")
public class QuotesController {

    private final InMemoryStore store;

    public QuotesController(final InMemoryStore store) {
        this.store = store;
    }

    @Get("/{symbol}")
    public HttpResponse getQuote(@PathVariable String symbol) {
        final Optional<Quote> maybeQuote = store.fetchQuote(symbol);

        if(maybeQuote.isEmpty()){
            final CustomError notFoundError = CustomError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("Quote for symbol not found")
                    .path("/quotes/" +symbol)
                    .build();
            return HttpResponse.notFound(notFoundError);
        }
        return HttpResponse.ok(maybeQuote.get());
    }
}
