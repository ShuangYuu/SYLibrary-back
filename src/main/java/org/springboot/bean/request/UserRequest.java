package org.springboot.bean.request;

import lombok.Data;

@Data
public class UserRequest extends BaseRequestPage{
    private String username;
}
