package com.atmservice.model;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawDTO {
    @ApiParam(defaultValue = "true")
    private Boolean confirmed;

    @ApiParam(defaultValue = "12500")
    private Double amount;

}
