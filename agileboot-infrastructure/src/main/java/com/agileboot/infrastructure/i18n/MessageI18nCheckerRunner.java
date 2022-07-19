package com.agileboot.infrastructure.i18n;

import cn.hutool.core.util.ArrayUtil;
import com.agileboot.common.core.exception.errors.BusinessErrorCode;
import com.agileboot.common.core.exception.errors.ClientErrorCode;
import com.agileboot.common.core.exception.errors.ErrorCodeInterface;
import com.agileboot.common.core.exception.errors.ExternalErrorCode;
import com.agileboot.common.core.exception.errors.InternalErrorCode;
import com.agileboot.common.utils.i18n.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 检测 未添加到i18n文件(messages.properties)中的message
 * @author valarchie
 */
@Component
@Slf4j
public class MessageI18nCheckerRunner implements ApplicationRunner {

    public static Object[] allErrorCodes = ArrayUtil.addAll(
        InternalErrorCode.values(),
        ExternalErrorCode.values(),
        ClientErrorCode.values(),
        BusinessErrorCode.values());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        checkEveryMessage();
    }

    /**
     * 如果想支持i18n, 请把对应的错误码描述填到 /resources/i18n/messages.properties 文件中
     */
    public void checkEveryMessage() {
        for (Object errorCode : allErrorCodes) {
            ErrorCodeInterface errorInterface = (ErrorCodeInterface)errorCode;
            try {
                String message = MessageUtils.message(errorInterface.i18n());
            } catch (Exception e) {
                log.warn(" in the file /resources/i18n/messages.properties, could not find i18n message for:"
                    + errorInterface.i18n());
            }
        }
    }
}
