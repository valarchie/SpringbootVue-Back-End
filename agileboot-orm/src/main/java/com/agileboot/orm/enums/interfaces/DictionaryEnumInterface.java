package com.agileboot.orm.enums.interfaces;

/**
 * 字典类型 接口
 * @author valarchie
 */
public interface DictionaryEnumInterface<T> {

    /**
     * @return 获取枚举的值
     */
    T getValue();

    /**
     *
     * @return 获取枚举的描述
     */
    String description();

    /**
     *
     * @return 获取css标签
     */
    String cssTag();


}
