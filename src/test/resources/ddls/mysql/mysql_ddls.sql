CREATE TABLE `utdatagen_utdb`.`test_table` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `varchar_column` VARCHAR(45) NULL,
  `numeric_column` DECIMAL(5,2) NULL,
  `timestamp_column` DATETIME NULL,
  `date_column` DATE NULL,
  `int_column` INT NULL,
  `char_column` CHAR(20) NULL,
  `boolean_column` TINYINT NULL,
  `float_column` FLOAT NULL,
  PRIMARY KEY (`id`));
