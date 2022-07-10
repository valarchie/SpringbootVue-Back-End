package com.agileboot.admin.response;

import lombok.Data;

/**
 * @author valarchie
 */
@Data
public class CaptchaDTO {

    private Boolean captchaOnOff;
    private String uuid;
    private String img;

}
