package de.oschirmer.gymtp.coverplan.fetch;


import java.util.function.Consumer;

public class Request<T> {

    private Consumer<T> consumer;
    private String url;

    public Request(Consumer<T> consumer, String url) {
        this.consumer = consumer;
        this.url = url;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public String getUrl() {
        return url;
    }
}