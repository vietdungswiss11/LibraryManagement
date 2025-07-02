package Ecommerce.BookWeb.Project.Controller.Rest;

import Ecommerce.BookWeb.Project.DTO.AddressDTO;
import Ecommerce.BookWeb.Project.DTO.ApiResponse;
import Ecommerce.BookWeb.Project.Model.Address;
import Ecommerce.BookWeb.Project.Model.User;
import Ecommerce.BookWeb.Project.Repository.AddressRepository;
import Ecommerce.BookWeb.Project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public AddressController(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    // Lấy tất cả địa chỉ của một user
    @GetMapping("/user/{userId}")
    public List<Address> getAddressesByUserId(@PathVariable int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        return addressRepository.findByUser(user);
    }

    // Lấy địa chỉ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable int id) {
        return addressRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Thêm địa chỉ mới cho user
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> addAddress(@PathVariable int userId, @RequestBody Address address) {
        return userRepository.findById(userId)
                .map(user -> {
                    address.setUser(user);
                    // Nếu đây là địa chỉ đầu tiên, đặt làm mặc định
                    if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
                        address.setDefault(true);
                    } else if (address.isDefault()) {
                        // Nếu đặt làm mặc định, bỏ mặc định của các địa chỉ khác
                        user.getAddresses().forEach(addr -> addr.setDefault(false));
                    }
                    return ResponseEntity.ok(new ApiResponse(true, "Đã lưu thành công!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Cập nhật địa chỉ
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable int id, @RequestBody Address addressDetails) {
        return addressRepository.findById(id)
                .map(address -> {
                    address.setAddressLine(addressDetails.getAddressLine());
                    address.setRecipientName(addressDetails.getRecipientName());
                    address.setPhoneNumber(addressDetails.getPhoneNumber());
                    
                    // Nếu đặt làm mặc định
                    if (addressDetails.isDefault() && !address.isDefault()) {
                        // Bỏ mặc định của các địa chỉ khác
                        User user = address.getUser();
                        user.getAddresses().forEach(addr -> {
                            if (addr.getId() != id) {
                                addr.setDefault(false);
                            }
                        });
                        address.setDefault(true);
                    }
                    
                    return ResponseEntity.ok(new ApiResponse(true, "Đã lưu thành công!"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Xóa địa chỉ
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable int id) {
        return addressRepository.findById(id)
                .map(address -> {
                    // Nếu là địa chỉ mặc định và còn địa chỉ khác, đặt một địa chỉ khác làm mặc định
                    if (address.isDefault() && address.getUser().getAddresses().size() > 1) {
                        address.getUser().getAddresses().stream()
                                .filter(addr -> addr.getId() != id)
                                .findFirst()
                                .ifPresent(addr -> {
                                    addr.setDefault(true);
                                    addressRepository.save(addr);
                                });
                    }
                    addressRepository.delete(address);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
