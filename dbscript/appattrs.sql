CREATE  TABLE `appattrs` (
  `id` VARCHAR(100) NOT NULL  COMMENT 'PK ',
  `aid` VARCHAR(100) NOT NULL  COMMENT '应用Pk关联 ',
  `snap` VARCHAR(800) NULL  COMMENT '应用截图地址 ',
  PRIMARY KEY (`id`) )
COMMENT = '应用属性表';

--INSERT INTO `appattrs` (`id`,`aid`,`snap`) VALUES();