package com.bnsantos.fyber.robolectric;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;

import rx.Scheduler;
import rx.plugins.RxJavaSchedulersHook;
import rx.plugins.RxJavaTestPlugins;
import rx.schedulers.Schedulers;

/**
 * Created by bruno on 22/11/15.
 */
public class RxJavaTestRunner extends RobolectricGradleTestRunner {
    public RxJavaTestRunner(Class<?> klass) throws InitializationError {
        super(klass);

        RxJavaTestPlugins.resetPlugins();
        RxJavaTestPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });
    }
}