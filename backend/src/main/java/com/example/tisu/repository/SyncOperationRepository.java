package com.example.tisu.repository;

import com.example.tisu.entity.SyncOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncOperationRepository extends JpaRepository<SyncOperation, String> {
    List<SyncOperation> findByUserIdAndServerTimestampGreaterThanOrderByServerTimestampAsc(String userId, long since);
}
