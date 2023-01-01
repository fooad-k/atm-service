package com.atmservice.model;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginDTO {
    @ApiParam(required = true,defaultValue = "pin")
    @NotNull(message = "Authentication Type is necessary")
    private String authenticationType;

    @ApiParam(defaultValue = "5225")
    @Size(max = 4 , min = 4, message = "You have to enter 4 digit")
    private String pin;

    @ApiParam(defaultValue = "jnhhhjjgfhfydgfg")
    private String fingerPrint;


}
