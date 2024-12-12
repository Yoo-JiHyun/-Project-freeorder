package com.aloha.freeorder.controller.pos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aloha.freeorder.domain.Order;
import com.aloha.freeorder.domain.SalesReport;
import com.aloha.freeorder.service.OrderService;

import lombok.extern.slf4j.Slf4j;

/**
 * REST 형식 컨트롤러
 * CRUD 비동기 처리
 * 
 */
@Slf4j
@RestController
@RequestMapping("/pos/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    /**
     * 주문 목록
     * @return
     */
    @GetMapping()
    public ResponseEntity<?> getAll() {
        log.info("주문 목록 조회");
        try {
            List<Order> orderList = orderService.list();
            return new ResponseEntity<>(orderList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 주문 조회
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        log.info("주문 조회");
        try {
            Order order = orderService.read(id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            log.error("주문 조회 중 에러 발생", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 주문 등록 처리
     * @param order
     * @return
     */
    @PostMapping()
    public ResponseEntity<?> create(Order order) {
        log.info("[POST] - /orders");
        try {
            int result = orderService.insert(order);
            if( result > 0 )
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            else{
                log.info("주문 DB에 등록 중 에러 발생...");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);    
            }
        } catch (Exception e) {
            log.error("주문 등록 중 에러 발생", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 주문 수정 처리
     * @param order
     * @return
     */
    @PutMapping()
    public ResponseEntity<?> update(Order order) {
        log.info("주문 수정");
        try {
            int result = orderService.insert(order);
            if( result > 0 )
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            else{
                log.info("주문 DB에 수정 중 에러 발생...");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);    
            }
        } catch (Exception e) {
            log.error("주문 수정 중 에러 발생", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 주문 삭제 처리
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> destroy(@PathVariable("id") String id) {
        log.info("주문 삭제");
        try {
            int result = orderService.delete(id);
            if( result > 0 )
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            else{
                log.info("주문 DB에 등록 중 에러 발생...");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);    
            }
        } catch (Exception e) {
            log.error("주문 삭제 중 에러 발생...", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     /**
     * 월별 매출 조회
     * @param status
     * @param day
     * @return
     */
    @GetMapping("/month")
    public ResponseEntity<?> getMonthlySales(
            @RequestParam("status") String status,
            @RequestParam("day") String day) {
        log.info("월별 매출 조회");
        try {
            SalesReport salesReport = orderService.totalMonth(status, day);
            return new ResponseEntity<>(salesReport, HttpStatus.OK);
        } catch (Exception e) {
            log.error("월별 매출 조회 중 에러 발생", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 일별 매출 조회
     * @param status
     * @param day
     * @return
     */
    @GetMapping("/day")
    public ResponseEntity<?> getDailySales(
            @RequestParam("status") String status,
            @RequestParam("day") String day) {
        log.info("일별 매출 조회");
        try {
            SalesReport salesReport = orderService.totalDay(status, day);
            return new ResponseEntity<>(salesReport, HttpStatus.OK);
        } catch (Exception e) {
            log.error("일별 매출 조회 중 에러 발생", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

