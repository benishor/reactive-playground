import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Adrian Scripca
 */
public class Main {

    public static void main(String[] args) {
        print(repeatCountTimesStreamBlocking(), "repeatingEventsBlocking");
        print(repeatCountTimesStreamAsync(), "repeatingEventsNonBlocking");
    }

    private static Observable<String> repeatCountTimesStreamBlocking() {
        return Observable.just("hello").repeat(5);
    }

    private static Observable<String> repeatCountTimesStreamAsync() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        Scheduler scheduler = Schedulers.from(executor);
        return Observable.just("hello").repeat(5, scheduler).doOnCompleted(new Action0() {
            public void call() {
                // otherwise we'll hang the main thread
                executor.shutdown();
            }
        });
    }

    private static void print(Observable<String> stream, final String streamName) {
        log(streamName, "before subscribing to stream");

        stream.subscribe(new Action1<String>() {
            public void call(String value) {
                log(streamName, value);
            }
        });

        log(streamName, "after subscribing to stream");
        System.out.println("------------------------");
    }

    private static void log(String streamName, String value) {
        System.out.println(MessageFormat.format("[{0}({1})] {2} [{3}] {4}",
                Thread.currentThread().getName(),
                Thread.currentThread().getId(),
                new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(System.currentTimeMillis())),
                streamName,
                value
        ));
    }
}
