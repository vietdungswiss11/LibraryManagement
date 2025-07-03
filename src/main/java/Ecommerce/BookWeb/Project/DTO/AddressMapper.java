package Ecommerce.BookWeb.Project.DTO;

import Ecommerce.BookWeb.Project.Model.Address;
import Ecommerce.BookWeb.Project.Model.Role;
import Ecommerce.BookWeb.Project.Model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AddressMapper {
    public AddressDTO toAddressDTO(Address address) {
        if(address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine(address.getAddressLine());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setRecipientName(address.getRecipientName());
        dto.setDefault(address.isDefault());


        if(address.getUser() != null) {
            dto.setUserDTO(toUserDTO(address.getUser()));
        }
        return dto;
    }

    public UserDTO toUserDTO(User user) {
        if(user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getRoles().stream()
                .map(this::toRoleDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public RoleDTO toRoleDTO(Role role) {
        if(role == null) {
            return null;
        }
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        return dto;
    }
}
