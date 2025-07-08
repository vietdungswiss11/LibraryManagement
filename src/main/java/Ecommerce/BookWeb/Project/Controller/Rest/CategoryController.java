package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.CategoryDTO;
import Ecommerce.BookWeb.Project.DTO.CategoryMapper;
import Ecommerce.BookWeb.Project.Model.Category;
import Ecommerce.BookWeb.Project.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    // Lấy tất cả danh mục
    @GetMapping
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Tìm kiếm danh mục theo từ khóa
    @GetMapping("/search")
    public List<CategoryDTO> searchCategories(@RequestParam String keyword) {
        return categoryRepository.searchByName(keyword).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Lấy danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable int id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    // Tạo mới danh mục
//    @PostMapping
//    public CategoryDTO createCategory(@RequestBody CategoryDTO categoryDTO) {
//        Category category = categoryMapper.toEntity(categoryDTO);
//        Category savedCategory = categoryRepository.save(category);
//        return categoryMapper.toDTO(savedCategory);
//    }
//
//    // Cập nhật danh mục
//    @PutMapping("/{id}")
//    public ResponseEntity<CategoryDTO> updateCategory(
//            @PathVariable int id,
//            @RequestBody CategoryDTO categoryDTO) {
//
//        return categoryRepository.findById(id)
//                .map(existingCategory -> {
//                    existingCategory.setName(categoryDTO.getName());
//                    Category updatedCategory = categoryRepository.save(existingCategory);
//                    return ResponseEntity.ok(categoryMapper.toDTO(updatedCategory));
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // Xóa danh mục
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteCategory(@PathVariable int id) {
//        return categoryRepository.findById(id)
//                .map(category -> {
//                    categoryRepository.delete(category);
//                    return ResponseEntity.ok().build();
//                })
//                .orElse(ResponseEntity.notFound().build());
//    }


}