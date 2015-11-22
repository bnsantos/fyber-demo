package rx.plugins;

import rx.plugins.RxJavaPlugins;

/**
 * Created by bruno on 22/11/15.
 */
public class RxJavaTestPlugins extends RxJavaPlugins {
    public RxJavaTestPlugins() {
        super();
    }

    public static void resetPlugins() {
        getInstance().reset();
    }
}

