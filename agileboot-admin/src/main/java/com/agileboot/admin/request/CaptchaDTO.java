package com.agileboot.admin.request;

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
