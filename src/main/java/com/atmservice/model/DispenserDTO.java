package com.atmservice.model;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DispenserDTO {
    @ApiParam(defaultValue = "true")
    private Boolean confirmed;

    @ApiParam(defaultValue = "12500")
    private Double amount;

}
