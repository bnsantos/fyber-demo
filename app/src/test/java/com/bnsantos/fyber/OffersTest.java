package com.bnsantos.fyber;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bnsantos.fyber.activities.MainActivity;
import com.bnsantos.fyber.exceptions.BadRequestException;
import com.bnsantos.fyber.exceptions.InvalidResponseException;
import com.bnsantos.fyber.model.BadRequest;
import com.bnsantos.fyber.model.Offer;
import com.bnsantos.fyber.model.OfferResponse;
import com.bnsantos.fyber.provider.RequestUtil;
import com.bnsantos.fyber.robolectric.RxJavaTestRunner;
import com.bnsantos.fyber.robolectric.TestApp;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static org.junit.Assert.*;


@RunWith(RxJavaTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, application = TestApp.class)
public class OffersTest {
    protected MainActivity activity;

    @BeforeClass
    public static void setUpRxAndroid() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
        ShadowLog.stream = System.out;
    }

    @Test
    public void testEmptyOffers() {
        Mockito.when(((App) RuntimeEnvironment.application).getProvider().getOffers(Matchers.anyMap())).thenAnswer(new Answer<Observable<OfferResponse>>() {
            @Override
            public Observable<OfferResponse> answer(InvocationOnMock invocation) throws Throwable {
                OfferResponse offerResponse = new OfferResponse();
                offerResponse.setCode("");
                offerResponse.setCount(0);
                offerResponse.setOffers(new ArrayList<Offer>());
                offerResponse.setPages(0);
                return Observable.just(offerResponse);
            }
        });
        activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        waitObservable();
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recyclerView);
        Assert.assertNotNull(recyclerView);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testManyOffers() {
        App application = (App) RuntimeEnvironment.application;
        Mockito.when(application.getProvider().getOffers(Matchers.anyMap())).thenAnswer(new Answer<Observable<Response>>() {
            @Override
            public Observable<Response> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(new Response("getOffers", 200, "reason", new ArrayList<Header>(), null));
            }
        });

        Mockito.when(application.getRequestUtil().extractAndValidateResponse(Matchers.any(Gson.class), Matchers.any(Response.class), Matchers.anyString()))
                .thenAnswer(new Answer<OfferResponse>() {
                    @Override
                    public OfferResponse answer(InvocationOnMock invocation) throws Throwable {
                        OfferResponse offerResponse = new OfferResponse();
                        offerResponse.setCode("");
                        offerResponse.setCount(30);
                        ArrayList<Offer> offers = new ArrayList<>();
                        for (int i = 0; i < 30; i++) {
                            Offer object = new Offer();
                            object.setTitle("Offer test " + i);
                            object.setTeaser("Offer teaser " + i);
                            object.setLink("Offer link " + i);
                            offers.add(object);
                        }
                        offerResponse.setOffers(offers);
                        offerResponse.setPages(1);
                        return offerResponse;
                    }
                });
        activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        waitObservable();
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recyclerView);
        Assert.assertNotNull(recyclerView);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(31, adapter.getItemCount());
    }

    @Test
    public void testInvalidHeader() {
        App application = (App) RuntimeEnvironment.application;
        Mockito.when(application.getProvider().getOffers(Matchers.anyMap())).thenAnswer(new Answer<Observable<Response>>() {
            @Override
            public Observable<Response> answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(new Response("getOffers", 200, "reason", new ArrayList<Header>(), null));
            }
        });

        Mockito.when(application.getRequestUtil().extractAndValidateResponse(Matchers.any(Gson.class), Matchers.any(Response.class), Matchers.anyString()))
                .thenAnswer(new Answer<OfferResponse>() {
                    @Override
                    public OfferResponse answer(InvocationOnMock invocation) throws Throwable {
                        throw new InvalidResponseException("No header validator on response", R.string.error_invalid_response);
                    }
                });
        activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        waitObservable();
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recyclerView);
        Assert.assertNotNull(recyclerView);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(1, adapter.getItemCount());
        Assert.assertEquals(activity.getString(R.string.error_invalid_response), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testBadRequest() {
        Mockito.when(((App) RuntimeEnvironment.application).getProvider().getOffers(Matchers.anyMap())).thenAnswer(new Answer<Observable<OfferResponse>>() {
            @Override
            public Observable<OfferResponse> answer(InvocationOnMock invocation) throws Throwable {
                BadRequest badRequest = new BadRequest();
                badRequest.setCode("ERROR_INVALID_TIMESTAMP");
                badRequest.setMessage("An invalid or expired timestamp was given as a parameter in the request.");
                return Observable.error(new BadRequestException("Bad Request", badRequest));
            }
        });
        activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        waitObservable();
        RecyclerView recyclerView = (RecyclerView) activity.findViewById(R.id.recyclerView);
        Assert.assertNotNull(recyclerView);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Assert.assertNotNull(adapter);
        Assert.assertEquals(1, adapter.getItemCount());
    }

    private void waitObservable() {
        // Flush all worker tasks out of queue and force them to execute.
        Robolectric.flushBackgroundThreadScheduler();
        Robolectric.getBackgroundThreadScheduler().idleConstantly(true);

        // A latch used to lock UI Thread.
        final CountDownLatch lock = new CountDownLatch(1);

        try {
            lock.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            lock.notifyAll();
        }

        // Flush all UI tasks out of queue and force them to execute.
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.getForegroundThreadScheduler().idleConstantly(true);
    }
}