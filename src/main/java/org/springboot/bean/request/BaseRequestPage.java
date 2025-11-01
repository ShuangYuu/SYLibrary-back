package org.springboot.bean.request;

import lombok.Data;

@Data
public class BaseRequestPage {
    private Integer pageNum = 1;
    private Integer pageSize = 15;
}
