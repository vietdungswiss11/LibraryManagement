package Ecommerce.BookWeb.Project.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDTO {
    private int id;
    private String name;
    private List<BookDTO> books;

}