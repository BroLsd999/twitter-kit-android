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

package com.example.app.tweetui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;

import com.example.app.BaseActivity;
import com.example.app.R;

public class TweetUiMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweetui_activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.kit_tweetui);
        }

        final Button xmlTweetButton = (Button) findViewById(R.id.button_xml_tweet_activity);
        xmlTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TweetUiMainActivity.this, XmlTweetActivity.class));
            }
        });

        final Button tweetActivityButton = (Button) findViewById(R.id.button_tweet_activity);
        tweetActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TweetUiMainActivity.this, TweetActivity.class));
            }
        });

        final Button unqiueTweetButton = (Button) findViewById(R.id.button_unique_tweet_activity);
        unqiueTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TweetUiMainActivity.this, UniqueTweetActivity.class));
            }
        });

        final Button tweetListButton = (Button) findViewById(R.id.button_fixed_timeline_activity);
        tweetListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TweetUiMainActivity.this, FixedTimelineActivity.class));
            }
        });

        final Button timelineButton = (Button) findViewById(R.id.button_refresh_timeline_activity);
        timelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TweetUiMainActivity.this, TimelineActivity.class));
            }
        });

        final Button timelinesButton = (Button) findViewById(R.id.button_timelines_activity);
        timelinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TweetUiMainActivity.this, TimelinesActivity.class));
            }
        });

        final Button tweetSelectorButton = (Button) findViewById(
                R.id.button_tweet_preview_activity);
        tweetSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TweetUiMainActivity.this, TweetPreviewActivity.class));
            }
        });

        final Button tweetPojoButton = (Button) findViewById(
                R.id.button_tweet_pojo_activity);
        tweetPojoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TweetUiMainActivity.this, TweetPojoActivity.class));
            }
        });
    }
}
