package com.agileboot.orm.enums.cache;

import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.enums.interfaces.DictionaryEnumInterface;
import lombok.Data;

/**
 * 字典模型类
 * @author valarchie
 */
@Data
public class DictionaryData {

    private String label;
    private String value;
    private String cssTag;

    @SuppressWarnings("rawtypes")
    public DictionaryData(DictionaryEnumInterface enumType) {
        if (enumType != null) {
            this.label = enumType.description();
            this.value = StrUtil.toString(enumType.getValue());
            this.cssTag = enumType.cssTag();
        }
    }

}
