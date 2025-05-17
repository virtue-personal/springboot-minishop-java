package com.virtue.app.repository;

import com.virtue.app.domain.Member;
import com.virtue.app.domain.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query(value = "SELECT s FROM Sales s JOIN FETCH s.member")
    List<Sales> customFindALl();

    List<Sales> findByMemberOrderByCreatedAtDesc(Member member);
    
    Page<Sales> findByMember(Member member, Pageable pageable);
}
