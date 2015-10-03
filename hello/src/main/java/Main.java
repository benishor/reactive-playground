import rx.Observable;
import rx.functions.Action1;

import java.text.MessageFormat;

/**
 * @author Adrian Scripca
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("before");
        hello("Hello", "there", "dear", "world!");
        System.out.println("after");
    }

    private static void hello(String... names) {
        Observable.from(names).subscribe(new Action1<String>() {
            public void call(String name) {
                System.out.println(
                        MessageFormat.format("[{0}({1})] {2}",
                                Thread.currentThread().getName(),
                                Thread.currentThread().getId(),
                                name));
            }
        });
    }
}
