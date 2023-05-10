package com.github.cb2222124.rtms.repository;

import com.github.cb2222124.rtms.model.TaxClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxClassRepository extends JpaRepository<TaxClass, Long> {
}
