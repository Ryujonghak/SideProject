package com.example.backend.controller;

import com.example.backend.model.Goods;
import com.example.backend.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName : com.example.backend.controller
 * fileName : GoodsController
 * author : hyuk
 * date : 2023/01/25
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/25         hyuk          최초 생성
 */
@Slf4j
@CrossOrigin("http://localhost")
@RestController
@RequestMapping("/api")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @GetMapping("/goods")
    public ResponseEntity<Object> getGoods(@RequestParam(required = false) String name,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "8") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Goods> goodsPage;

//            TODO: 검색어 입력값에 따라서 다른 결과 조회할 예정 ex) 검색어 넣고 검색하면 like 검색 / 검색어 없이 입력하면 전체 조회하는 형식
            if (name.isEmpty() == false) {
                goodsPage = goodsService.findAllByNameContaining(name, pageable);
            } else {
                goodsPage = goodsService.findAll(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("goods", goodsPage.getContent());
            response.put("currentPage", goodsPage.getNumber());
            response.put("totalItems", goodsPage.getTotalElements());
            response.put("totalPages", goodsPage.getTotalPages());

            if (goodsPage.isEmpty() == false) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/goods")
    public ResponseEntity<Object> createGoods(@RequestBody Goods goods) {
        try {
            Goods goods2 = goodsService.save(goods);

            return new ResponseEntity<>(goods2, HttpStatus.OK);
        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/goods/{gid}")
    public ResponseEntity<Object> updateGoods(@PathVariable int gid, @RequestBody Goods goods) {
        try {
            Goods goods2 = goodsService.save(goods);

            return new ResponseEntity<>(goods2, HttpStatus.OK);
        } catch (Exception e) {
            log.debug(e.getMessage());

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/goods/deletion/{gid}")
    public ResponseEntity<Object> deleteById(@PathVariable int gid) {
        try {
            boolean success = goodsService.removeById(gid);

            if (success == true) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            log.debug(e.getMessage());

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/goods/deletion/all")
    public ResponseEntity<Object> removeAll() {
        try {
            goodsService.removeAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
