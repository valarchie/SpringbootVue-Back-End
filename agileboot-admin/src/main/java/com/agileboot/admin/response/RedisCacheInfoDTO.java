package com.agileboot.admin.response;

import java.util.List;
import java.util.Properties;
import lombok.Data;

@Data
/**
 * @author valarchie
 */
public class RedisCacheInfoDTO {

    private Properties info;
    private Object dbSize;
    private List<CommonStatusDTO> commandStats;




    @Data
    public static class CommonStatusDTO {
        private String name;
        private String value;
    }

}
