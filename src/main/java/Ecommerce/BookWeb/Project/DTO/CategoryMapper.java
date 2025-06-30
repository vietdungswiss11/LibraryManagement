package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Book;
import Ecommerce.BookWeb.Project.Model.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        if (category.getBooks() != null) {
            dto.setBooks(category.getBooks().stream()
                    .map(this::toBookDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private CategoryDTO.BookDTO toBookDTO(Book book) {
        if (book == null) {
            return null;
        }

        CategoryDTO.BookDTO bookDTO = new CategoryDTO.BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setDiscountPrice(book.getDiscountPrice());

        if (book.getImages() != null && !book.getImages().isEmpty()) {
            bookDTO.setImageUrl(book.getImages().get(0).getUrl());
        }

        return bookDTO;
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());

        return category;
    }
}