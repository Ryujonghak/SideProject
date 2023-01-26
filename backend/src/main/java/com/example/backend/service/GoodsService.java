package com.example.backend.service;

import com.example.backend.model.Goods;
import com.example.backend.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * packageName : com.example.backend.security.services
 * fileName : GoodsService
 * author : hyuk
 * date : 2023/01/25
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/25         hyuk          최초 생성
 */
@Service
@Slf4j
public class GoodsService {

    @Autowired
    GoodsRepository goodsRepository;

    /** 전체 상품조회 함수 (페이징처리) */
    public Page<Goods> findAll(Pageable pageable) {
        Page<Goods> page = goodsRepository.findAll(pageable);

        return page;
    }

    /** pk값으로 해당 상품 검색 함수 */
    public Optional<Goods> findById(int gid) {
        Optional<Goods> optionalGoods = goodsRepository.findById(gid);

        return optionalGoods;
    }

    /** 상품명으로 해당 상품 like 검색 함수 */
    public Page<Goods> findAllByNameContaining(String name, Pageable pageable) {
        Page<Goods> optionalGoods2 = goodsRepository.findAllByNameContaining(name, pageable);

        return optionalGoods2;
    }

    /** 전체 상품 삭제 함수 */
    public void removeAll() {
        goodsRepository.deleteAll();
    }

    /** pk값으로 해당 상품 삭제 함수 */
    public boolean removeById(int gid) {
        if (goodsRepository.existsById(gid) == true) {
            goodsRepository.deleteById(gid);

            return true;
        }

        return false;
    }

    /** 상품 정보 저장 함수 */
    public Goods save(Goods goods) {
        Goods goods2 = goodsRepository.save(goods);
        return goods2;
    }

}
