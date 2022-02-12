package com.onfree.core.repository;

import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioContentRepository extends JpaRepository<PortfolioContent, Long> {
}
