package com.example.backend.repository;

import com.example.backend.model.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * packageName : com.example.backend.repository
 * fileName : GoodsRepository
 * author : hyuk
 * date : 2023/01/25
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/25         hyuk          최초 생성
 */
public interface GoodsRepository extends JpaRepository<Goods, Integer> {

    Page<Goods> findAllByNameContaining(String name, Pageable pageable);
}
