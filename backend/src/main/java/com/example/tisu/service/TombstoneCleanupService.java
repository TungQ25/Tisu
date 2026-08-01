package com.example.tisu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TombstoneCleanupService {
    public interface TombstoneCleaner {

        int purge(long cutoff);
    }

    // TODO: làm nốt tombstone cho từng chức năng liên quan, đây chỉ là form dựng trước:
    //HabitTombstoneCleaner
    //CategoryTombstoneCleaner
    //TaskTombstoneCleaner

    private final List<TombstoneCleaner> cleaners;
    private final long retentionMillis;

    public TombstoneCleanupService(
            List<TombstoneCleaner> cleaners,
            @Value("${app.tombstone-retention-ms}") long retentionMillis
    ) {
        this.cleaners = cleaners;
        this.retentionMillis = Math.max(0L, retentionMillis);
    }

    @Scheduled(
            initialDelayString = "${app.tombstone-cleanup-delay-ms}",
            fixedDelayString = "${app.tombstone-cleanup-delay-ms}"
    )
    @Transactional
    public void purgeExpiredTombstones() {
        long cutoff = System.currentTimeMillis() - retentionMillis;

        for (TombstoneCleaner cleaner : cleaners) {
            cleaner.purge(cutoff);
        }
    }
}
