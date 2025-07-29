package com.vijay.ms.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class PrefixGenerator {

    private static final ConcurrentHashMap<String, AtomicLong> dailyCounters = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AtomicLong> simpleCounters = new ConcurrentHashMap<>();

    // Format: ORD-00001
    public static String generateSequential(String prefix) {
        AtomicLong counter = simpleCounters.computeIfAbsent(prefix, k -> new AtomicLong(0));
        long next = counter.incrementAndGet();
        return String.format("%s-%05d", prefix.toUpperCase(), next);
    }

    // Format: ORD-20250630-001
    public static String generateDateBased(String prefix) {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = prefix + "-" + date;

        AtomicLong counter = dailyCounters.computeIfAbsent(key, k -> new AtomicLong(0));
        long next = counter.incrementAndGet();

        return String.format("%s-%s-%03d", prefix.toUpperCase(), date, next);
    }
}

