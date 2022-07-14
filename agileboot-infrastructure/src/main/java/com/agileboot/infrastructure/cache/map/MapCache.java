package com.agileboot.infrastructure.cache.map;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.agileboot.orm.enums.cache.DictionaryData;
import com.agileboot.orm.enums.dict.BusinessTypeEnum;
import com.agileboot.orm.enums.dict.CommonAnswerEnum;
import com.agileboot.orm.enums.dict.CommonStatusEnum;
import com.agileboot.orm.enums.dict.GenderEnum;
import com.agileboot.orm.enums.dict.NoticeStatusEnum;
import com.agileboot.orm.enums.dict.NoticeTypeEnum;
import com.agileboot.orm.enums.dict.OperationStatusEnum;
import com.agileboot.orm.enums.dict.VisibleStatusEnum;
import com.agileboot.orm.enums.interfaces.DictionaryEnumInterface;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本地一级缓存  使用Map
 *
 * @author valarchie
 */
public class MapCache {

    private final static Map<String, List<DictionaryData>> DICTIONARY_CACHE = MapUtil.newHashMap(128);

    static {
        initDictionaryCache();
    }

    private static void initDictionaryCache() {

        DICTIONARY_CACHE.put(BusinessTypeEnum.getDictName(), arrayToList(BusinessTypeEnum.values()));
        DICTIONARY_CACHE.put(CommonAnswerEnum.getDictName(), arrayToList(CommonAnswerEnum.values()));
        DICTIONARY_CACHE.put(CommonStatusEnum.getDictName(), arrayToList(CommonStatusEnum.values()));
        DICTIONARY_CACHE.put(GenderEnum.getDictName(), arrayToList(GenderEnum.values()));
        DICTIONARY_CACHE.put(NoticeStatusEnum.getDictName(), arrayToList(NoticeStatusEnum.values()));
        DICTIONARY_CACHE.put(NoticeTypeEnum.getDictName(), arrayToList(NoticeTypeEnum.values()));
        DICTIONARY_CACHE.put(OperationStatusEnum.getDictName(), arrayToList(OperationStatusEnum.values()));
        DICTIONARY_CACHE.put(VisibleStatusEnum.getDictName(), arrayToList(VisibleStatusEnum.values()));

    }

    public static Map<String, List<DictionaryData>> dictionaryCache() {
        return DICTIONARY_CACHE;
    }

    @SuppressWarnings("rawtypes")
    private static List<DictionaryData> arrayToList(DictionaryEnumInterface[] dictionaryEnums) {
        if(ArrayUtil.isEmpty(dictionaryEnums)) {
            return ListUtil.empty();
        }
        return Arrays.stream(dictionaryEnums).map(DictionaryData::new).collect(Collectors.toList());
    }


}
