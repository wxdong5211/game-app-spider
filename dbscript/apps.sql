CREATE  TABLE `apps` (
  `id` VARCHAR(100) NOT NULL  COMMENT 'PK ',
  `name` VARCHAR(100) NULL  COMMENT '名称 ',
  `catalog` VARCHAR(100) NULL COMMENT '大类别 可能为空' ,
  `subCatalog` VARCHAR(100) NULL  COMMENT '类别 ',
  `logo` VARCHAR(800) NULL  COMMENT 'LOGO地址 ',
  `version` VARCHAR(100) NULL  COMMENT '版本 ',
  `company` VARCHAR(100) NULL  COMMENT '厂商 ',
  `uri` VARCHAR(800) NULL  COMMENT '下载地址 ',
  `times` VARCHAR(100) NULL  COMMENT '下载次数含中文 ',
  `stars` VARCHAR(100) NULL  COMMENT '星级 ',
  `description` TEXT NULL  COMMENT '简介 ',
  `datetime` VARCHAR(100) NULL  COMMENT '上架或更新日期有可能空 ',
  `from` VARCHAR(100) NULL  COMMENT '本次抓取来源 ',
  `createTime` VARCHAR(100) NULL  COMMENT '本条数据抓取时间 ',
  PRIMARY KEY (`id`) )
COMMENT = '应用表';

--INSERT INTO `apps` (`id`,`name`,`catalog`,`subCatalog`,`logo`,`version`,`company`,`uri`,`times`,`stars`,`description`,`datetime`,`from`,`createTime`) VALUES();