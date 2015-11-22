package com.bnsantos.fyber.robolectric;

import android.content.Context;

import com.bnsantos.fyber.App;
import com.bnsantos.fyber.provider.OfferProvider;
import com.bnsantos.fyber.provider.RequestUtil;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bruno on 22/11/15.
 */
public class TestApp extends App {
    @Override
    protected OfferProvider initOfferProvider() {
        return Mockito.mock(OfferProvider.class);
    }

    @Override
    protected RequestUtil initRequestUtil() {
        RequestUtil mock = Mockito.mock(RequestUtil.class);
        Mockito.when(mock.generateQueryParams(Matchers.any(Context.class), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyInt())).thenAnswer(new Answer<Map<String, String>>() {
            @Override
            public Map<String, String> answer(InvocationOnMock invocation) throws Throwable {
                return new HashMap<String, String>();
            }
        });
        return mock;
    }
}
