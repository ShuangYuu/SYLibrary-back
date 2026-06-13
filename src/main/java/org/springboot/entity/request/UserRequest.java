package org.springboot.entity.request;

import lombok.Data;

@Data
public class UserRequest extends BaseRequestPage{
    private String username;
}
