CREATE  TABLE `appcomments` (
  `id` VARCHAR(100) NOT NULL  COMMENT 'PK ',
  `aid` VARCHAR(100) NOT NULL  COMMENT '应用Pk关联 ',
  `user` VARCHAR(100) NULL COMMENT '评论用户' ,
  `comment` TEXT NULL  COMMENT '评论',
  `datetime` VARCHAR(100) NULL  COMMENT '评论日期',
  PRIMARY KEY (`id`) )
COMMENT = '应用评论表';

--INSERT INTO `appcomments`(`id`,`aid`,`user`,`comment`,`datetime`) VALUES ();