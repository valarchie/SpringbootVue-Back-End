package com.ruoyi.common.utils.ip;

import cn.hutool.core.net.NetUtil;
import cn.hutool.http.HttpUtil;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import cn.hutool.core.util.StrUtil;

/**
 * query geography address from ip
 *
 * @author valarchie
 */
public class AddressUtils {

    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

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
        if (RuoYiConfig.isAddressEnabled()) {
            try {

                String rspStr = HttpUtil.get(ADDRESS_QUERY_SITE + "ip=" + ip + "&json=true",
                    Charset.forName(Constants.GBK));
                if (StrUtil.isEmpty(rspStr)) {
                    log.error("获取地理位置异常 {}", ip);
                    geographyAddress = UNKNOWN_ADDRESS;
                }
                JSONObject obj = JSONObject.parseObject(rspStr);
                String region = obj.getString("pro");
                String city = obj.getString("city");
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
