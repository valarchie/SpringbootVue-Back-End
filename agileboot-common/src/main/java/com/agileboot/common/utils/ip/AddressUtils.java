package com.agileboot.common.utils.ip;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.agileboot.common.config.AgileBootConfig;
import com.agileboot.common.constant.Constants;
import com.agileboot.common.utils.jackson.JacksonUtil;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

/**
 * query geography address from ip
 *
 * @author valarchie
 */
@Slf4j
public class AddressUtils {

    /**
     * website for query geography address from ip
     */
    public static final String ADDRESS_QUERY_SITE = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN_ADDRESS = "XX XX";

    public static String getRealAddressByIp(String ip) {

        String geographyAddress = UNKNOWN_ADDRESS;

        // no need to query address for inner ip
        if (NetUtil.isInnerIP(ip)) {
            geographyAddress = "internal IP";
        }
        if (AgileBootConfig.isAddressEnabled()) {
            try {

                String rspStr = HttpUtil.get(ADDRESS_QUERY_SITE + "ip=" + ip + "&json=true",
                    Charset.forName(Constants.GBK));
                if (StrUtil.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    geographyAddress = UNKNOWN_ADDRESS;
                }

                String region = JacksonUtil.getAsString(rspStr, "pro");
                String city = JacksonUtil.getAsString(rspStr, "city");
                geographyAddress = String.format("%s %s", region, city);
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return geographyAddress;
    }


    public static void main(String[] args) {

    }

}
