package com.ruoyi.framework.mybatisplus;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.ruoyi.common.core.controller.BaseController;
import java.util.Collections;

/**
 * @author valarchie
 */
public class CodeGenerator {

    public static void main(String[] args) {

        generateCode("/ruoyi-system");

    }

    public static void generateCode(String module) {

        FastAutoGenerator.create(
                new DataSourceConfig.Builder("jdbc:mysql://localhost:33066/ry-vue","root","Wds123123#")
//            .schema("mybatis-plus")
                    // all these three options
                    .dbQuery(new MySqlQuery())
                    .typeConvert(new MySqlTypeConvert())
                    .keyWordsHandler(new MySqlKeyWordsHandler()))
            .globalConfig(
                builder -> {
                    builder
                        // override old code of file
                        .fileOverride()
                        .outputDir(System.getProperty("user.dir") + module + "/src/main/java")
                        // use date type under package of java utils
                        .dateType(DateType.ONLY_DATE)
                        // 配置生成文件中的author
                        .author("valarchie")
//                    .enableKotlin()
                        // generate swagger annotations.
                        .enableSwagger()
                        // 注释日期的格式
                        .commentDate("yyyy-MM-dd")
                        .build();
                })
            .packageConfig(builder -> {
                builder
                    // parent package name
                    .parent("com.ruoyi.system.domain.test")
                    .moduleName("sys")
                    .entity("po")
                    .service("service")
                    .serviceImpl("service.impl")
                    .mapper("mapper")
                    .xml("mapper.xml")
                    .controller("controller")
                    .other("other")
                    // define dir related to OutputFileType(entity,mapper,service,controller,mapper.xml)
                    .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + module
                        + "/src/main/resources/mapper/system/test"))
                    .build();
                ;

            })
//            customization code template. disable if you don't have specific requirement.
//            .templateConfig(builder -> {
//                builder
//                    .disable(TemplateType.ENTITY)
//                    .entity("/templates/entity.java")
//                    .service("/templates/service.java")
//                    .serviceImpl("/templates/serviceImpl.java")
//                    .mapper("/templates/mapper.java")
//                    .mapperXml("/templates/mapper.xml")
//                    .controller("/templates/controller.java")
//                    .build();
//            })
            .injectionConfig(builder -> {
                // Customization
                builder.beforeOutputFile((tableInfo, objectMap) -> {
                        System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
                    })
//                    .customMap(Collections.singletonMap("test", "baomidou"))
//                    .customFile(Collections.singletonMap("test.txt", "/templates/test.vm"))
                    .build();
            })
            .strategyConfig(builder -> {
                builder
                    // Global Configuration
                    .enableCapitalMode()
                    // does not generate view
                    .enableSkipView()
                    .disableSqlFilter()
                    // filter which tables need to be generated
//                    .likeTable(new LikeTable("USER"))
//                    .addInclude("t_simple")
//                    .addTablePrefix("t_", "c_")
//                    .addFieldSuffix("_flag")
                    .addInclude("sys_menu")

                    // Entity Configuration
                    .entityBuilder()
//                    .superClass(BaseEntity.class)
//                    .disableSerialVersionUID()
//                    .enableChainModel()
                    .enableLombok()
                    // boolean field
//                    .enableRemoveIsPrefix()
                    .enableTableFieldAnnotation()
                    // operate entity like JPA.
                    .enableActiveRecord()
//                    .versionColumnName("version")
//                    .versionPropertyName("version")
//                    .logicDeleteColumnName("deleted")
//                    .logicDeletePropertyName("deleteFlag")
                    .naming(NamingStrategy.underline_to_camel)
                    .columnNaming(NamingStrategy.underline_to_camel)
//                    .addSuperEntityColumns("id", "created_by", "created_time", "updated_by", "updated_time")
//                    .addIgnoreColumns("age")
                    .addTableFills(new Column("create_time", FieldFill.INSERT))
                    .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                    // ID strategy AUTO, NONE, INPUT, ASSIGN_ID, ASSIGN_UUID;
                    .idType(IdType.AUTO)
                    .formatFileName("%sEntity")
                    .build()

                    // Controller settings
                    .controllerBuilder()
                    .superClass(BaseController.class)
                    .enableHyphenStyle()
                    .enableRestStyle()
                    .formatFileName("%sController")
                    .build()

                    // Mapper configuration
                    .mapperBuilder()
//                    .superClass(BaseMapper.class)
//                    .enableMapperAnnotation()
//                    .enableBaseResultMap()
//                    .enableBaseColumnList()
//                    .cache(MyMapperCache.class)
                    .formatMapperFileName("%sDao")
                    .formatXmlFileName("%sXml")
                    .build();
            })
            // 使用Freemarker引擎模板，默认的是Velocity引擎模板
            .templateEngine(new VelocityTemplateEngine())
            .execute();


    }

}
