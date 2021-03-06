/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetcomposer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;
import com.twitter.sdk.android.core.services.MediaService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TweetUploadServiceTest {
    private static final String EXPECTED_TWEET_TEXT = "tweet text";

    private Context context;
    private ComposerApiClient mockComposerApiClient;
    private StatusesService mockStatusesService;
    private ArgumentCaptor<Callback> callbackCaptor;
    private MediaService mockMediaService;
    private TweetUploadService.DependencyProvider mockDependencyProvider;
    private TweetUploadService service;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application;
        mockMediaService = mock(MediaService.class);
        mockStatusesService = mock(StatusesService.class);
        callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        mockComposerApiClient = mock(ComposerApiClient.class);
        when(mockComposerApiClient.getComposerStatusesService()).thenReturn(mockStatusesService);
        when(mockComposerApiClient.getMediaService()).thenReturn(mockMediaService);

        mockDependencyProvider = mock(TweetUploadService.DependencyProvider.class);
        when(mockDependencyProvider.getComposerApiClient(any(TwitterSession.class)))
                .thenReturn(mockComposerApiClient);

        when(mockDependencyProvider.getComposerApiClient(any(TwitterSession.class)))
                .thenReturn(mockComposerApiClient);
        service = spy(new TweetUploadService(mockDependencyProvider));
    }

    @Test
    public void testOnHandleIntent() {
        final TwitterAuthToken mockToken = mock(TwitterAuthToken.class);
        final Card mockCard = mock(Card.class);

        final Intent intent = new Intent(context, TweetUploadService.class);
        intent.putExtra(TweetUploadService.EXTRA_USER_TOKEN, mockToken);
        intent.putExtra(TweetUploadService.EXTRA_TWEET_TEXT, EXPECTED_TWEET_TEXT);
        intent.putExtra(TweetUploadService.EXTRA_TWEET_CARD, mockCard);
        service.onHandleIntent(intent);

        assertEquals(service.twitterSession.getAuthToken(), mockToken);
        assertEquals(service.tweetText, EXPECTED_TWEET_TEXT);
        assertEquals(service.tweetCard, mockCard);
        verify(service).uploadTweet(any(TwitterSession.class), eq(EXPECTED_TWEET_TEXT));
    }

    @Test
    public void testOnHandleIntent_withAppCard() {
        final TwitterAuthToken mockToken = mock(TwitterAuthToken.class);
        final Card appCard = new Card.AppCardBuilder(context).imageUri(mock(Uri.class)).build();

        final Intent intent = new Intent(context, TweetUploadService.class);
        intent.putExtra(TweetUploadService.EXTRA_USER_TOKEN, mockToken);
        intent.putExtra(TweetUploadService.EXTRA_TWEET_TEXT, EXPECTED_TWEET_TEXT);
        intent.putExtra(TweetUploadService.EXTRA_TWEET_CARD, appCard);
        service.onHandleIntent(intent);

        assertEquals(service.twitterSession.getAuthToken(), mockToken);
        assertEquals(service.tweetText, EXPECTED_TWEET_TEXT);
        assertEquals(service.tweetCard, appCard);
        verify(service).uploadAppCardTweet(any(TwitterSession.class), eq(EXPECTED_TWEET_TEXT),
                eq(appCard));
    }

    @Test
    public void testUploadTweet_success() {
        service.uploadTweet(mock(TwitterSession.class), EXPECTED_TWEET_TEXT);
        verify(mockStatusesService).update(eq(EXPECTED_TWEET_TEXT), isNull(String.class),
                callbackCaptor.capture());
        final Tweet tweet =  new TweetBuilder().setId(123L).build();
        callbackCaptor.getValue().success(new Result<>(tweet, null));
        verify(service).sendSuccessBroadcast(eq(123L));
        verify(service).stopSelf();
    }

    @Test
    public void testUploadTweet_failure() {
        service.uploadTweet(mock(TwitterSession.class), EXPECTED_TWEET_TEXT);
        verify(mockStatusesService).update(eq(EXPECTED_TWEET_TEXT), isNull(String.class),
                callbackCaptor.capture());
        callbackCaptor.getValue().failure(mock(TwitterException.class));
        verify(service).sendFailureBroadcast(any(Intent.class));
        verify(service).stopSelf();
    }

    @Test
    public void testSendSuccessBroadcast() {
        final ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        service.sendSuccessBroadcast(anyLong());
        verify(service).sendBroadcast(intentCaptor.capture());
        assertEquals(TweetUploadService.UPLOAD_SUCCESS, intentCaptor.getValue().getAction());
    }

    @Test
    public void testSendFailureBroadcast() {
        final Intent mockIntent = mock(Intent.class);
        final ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        service.sendFailureBroadcast(mockIntent);
        verify(service).sendBroadcast(intentCaptor.capture());
        assertEquals(TweetUploadService.UPLOAD_FAILURE, intentCaptor.getValue().getAction());
        assertEquals(mockIntent,
                intentCaptor.getValue().getParcelableExtra(TweetUploadService.EXTRA_RETRY_INTENT));
    }
}
