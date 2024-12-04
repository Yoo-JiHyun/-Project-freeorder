package com.aloha.freeorder.controller.pos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aloha.freeorder.domain.Category;
import com.aloha.freeorder.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * REST 형식 컨트롤러
 * CRUD 비동기 처리
 * 
 */
@RestController
@RequestMapping("/pos/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<?> list() throws Exception {
        List<Category> CategoryList = categoryService.list();
        return ResponseEntity.ok(CategoryList);
    }

    @GetMapping("/read")
    public ResponseEntity<?> read(@RequestParam("id") Long id) throws Exception {
        Category category = categoryService.read(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertPro(@RequestBody Category category) throws Exception {
        int result = categoryService.insert(category);
        if (result > 0) {
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.badRequest().body("error");
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePro(@RequestBody Category category) throws Exception {
        int result = categoryService.update(category);
        if(result > 0){
            return ResponseEntity.ok("seccess");
        }
        return ResponseEntity.badRequest().body("error");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id) throws Exception {
        int result = categoryService.delete(id);
        if(result > 0){
            return ResponseEntity.ok("seccess");
        }
        return ResponseEntity.badRequest().body("error");
    }
    
    
    
    
    
}
