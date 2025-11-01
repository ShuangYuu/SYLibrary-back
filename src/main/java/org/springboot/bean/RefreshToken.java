package org.springboot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshToken {

    private Integer id;
    private String type;
    private String jti;
    private String role;

    public RefreshToken(Integer id) {
        this.id = id;
    }
}
