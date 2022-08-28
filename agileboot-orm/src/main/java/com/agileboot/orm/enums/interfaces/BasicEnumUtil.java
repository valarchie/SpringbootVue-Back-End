package com.agileboot.orm.enums.interfaces;

import cn.hutool.core.convert.Convert;
import com.agileboot.common.exception.ApiException;
import com.agileboot.common.exception.errors.InternalErrorCode;
import java.util.Objects;

public class BasicEnumUtil {

    public static <E extends Enum<E>> E fromValue(Class<E> enumClass, Object value) {
        E target = null;

        for (Object enumConstant : enumClass.getEnumConstants()) {
            BasicEnum basicEnum = (BasicEnum) enumConstant;
            if (Objects.equals(basicEnum.getValue(), value)) {
                target = (E) basicEnum;
            }
        }

        if (target == null) {
            throw new ApiException(InternalErrorCode.GET_ENUM_FAILED);
        }

        return target;
    }

    public static <E extends Enum<E>> E fromBooleanValue(Class<E> enumClass, Boolean bool) {
        Integer value = Convert.toInt(bool, 0);
        return fromValue(enumClass, value);
    }

}
