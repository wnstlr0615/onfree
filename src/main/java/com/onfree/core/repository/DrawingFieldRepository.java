package com.onfree.core.repository;

import com.onfree.core.entity.DrawingField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DrawingFieldRepository extends JpaRepository<DrawingField, Long> {
    List<DrawingField> findAllByOrderByTopDesc();

    Optional<DrawingField> findByFieldName(String fieldName);
}
