package Ecommerce.BookWeb.Project.Repository;

import Ecommerce.BookWeb.Project.Model.Address;
import Ecommerce.BookWeb.Project.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUser(User user);
    List<Address> findByUserAndIsDefaultTrue(User user);
    Long countByUser(User user);
}
