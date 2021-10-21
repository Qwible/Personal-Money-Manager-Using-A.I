package com.example.android.roomwordssample

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
 * Once we got a notification via onChanged, we stop observing.
 */

