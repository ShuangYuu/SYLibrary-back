package org.springboot.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private Integer id;
    private String username;
    private String phone;
    private String email;

    public LoginDTO(Integer id) {
        this.id = id;
    }
}
