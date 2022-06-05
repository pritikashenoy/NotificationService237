package com.consumer.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RSSItemRepository extends JpaRepository<RSSItem, Long> {
}