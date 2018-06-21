-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema adapcompounddb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `adapcompounddb` DEFAULT CHARACTER SET latin1 ;
USE `adapcompounddb` ;

-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumCluster`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumCluster` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `ConsensusSpectrumId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `Diameter` DOUBLE NOT NULL,
  `Size` INT(11) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `SpectrumCluster_ConsensusSpectrumId_uindex` (`ConsensusSpectrumId` ASC),
  CONSTRAINT `SpectrumCluster_Spectrum_Id_fk`
  FOREIGN KEY (`ConsensusSpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 64
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionDisease`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionDisease` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` TEXT NOT NULL,
  `Description` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`))
  ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionSource`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionSource` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` TEXT NOT NULL,
  `Description` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`))
  ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SubmissionSpecimen`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SubmissionSpecimen` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` TEXT NOT NULL,
  `Description` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`))
  ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserPrincipal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserPrincipal` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Username` VARCHAR(30) NOT NULL,
  `Email` VARCHAR(30) NOT NULL,
  `HashedPassword` BINARY(60) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE INDEX `UserPrincipal_Username_uindex` (`Username` ASC))
  ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Submission`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Submission` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` TEXT NOT NULL,
  `Description` TEXT NULL DEFAULT NULL,
  `DateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Filename` TEXT NOT NULL,
  `FileType` VARCHAR(30) NOT NULL,
  `File` LONGBLOB NOT NULL,
  `UserPrincipalId` BIGINT(20) UNSIGNED NOT NULL,
  `SourceId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `SpecimenId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `DiseaseId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Submission_DateTime_Id_index` (`DateTime` ASC, `Id` ASC),
  INDEX `Submission_UserPrincipalId_index` (`UserPrincipalId` ASC),
  INDEX `Submission_SubmissionSource_Id_fk_idx` (`SourceId` ASC),
  INDEX `Submission_SubmissionSpecimen_Id_fk_idx` (`SpecimenId` ASC),
  INDEX `Submission_SubmissionDesease_Id_fk_idx` (`DiseaseId` ASC),
  CONSTRAINT `Submission_SubmissionDesease_Id_fk`
  FOREIGN KEY (`DiseaseId`)
  REFERENCES `adapcompounddb`.`SubmissionDisease` (`Id`),
  CONSTRAINT `Submission_SubmissionSource_Id_fk`
  FOREIGN KEY (`SourceId`)
  REFERENCES `adapcompounddb`.`SubmissionSource` (`Id`),
  CONSTRAINT `Submission_SubmissionSpecimen_Id_fk`
  FOREIGN KEY (`SpecimenId`)
  REFERENCES `adapcompounddb`.`SubmissionSpecimen` (`Id`),
  CONSTRAINT `Submission_UserPrincipal_Id_fk`
  FOREIGN KEY (`UserPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 15
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Spectrum`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Spectrum` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Name` TEXT NULL DEFAULT NULL,
  `Precursor` DOUBLE NULL DEFAULT NULL,
  `RetentionTime` DOUBLE NULL DEFAULT NULL,
  `SubmissionId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `ClusterId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `Consensus` TINYINT(1) NOT NULL DEFAULT '0',
  `Reference` TINYINT(1) NOT NULL DEFAULT '0',
  `ChromatographyType` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Spectrum_SubmissionId_index` (`SubmissionId` ASC),
  INDEX `Spectrum_ClusterId_index` (`ClusterId` ASC),
  INDEX `Spectrum_Consensus_index` (`Consensus` ASC),
  CONSTRAINT `Spectrum_SpectrumCluster_Id_fk`
  FOREIGN KEY (`ClusterId`)
  REFERENCES `adapcompounddb`.`SpectrumCluster` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `Spectrum_Submission_Id_fk`
  FOREIGN KEY (`SubmissionId`)
  REFERENCES `adapcompounddb`.`Submission` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3406
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`Peak`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`Peak` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Mz` DOUBLE NOT NULL,
  `Intensity` DOUBLE NOT NULL,
  `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Peak_Mz_index` (`Mz` ASC),
  INDEX `Peak_SpectrumId_index` (`SpectrumId` ASC),
  CONSTRAINT `Peak_Spectrum_Id_fk`
  FOREIGN KEY (`SpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 300587
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumMatch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumMatch` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `QuerySpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `MatchSpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `Score` DOUBLE NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumMatch_Spectrum_Id_fk_2` (`MatchSpectrumId` ASC),
  INDEX `SpectrumMatch_QuerySpectrumId_index` (`QuerySpectrumId` ASC),
  CONSTRAINT `SpectrumMatch_Spectrum_Id_fk`
  FOREIGN KEY (`QuerySpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `SpectrumMatch_Spectrum_Id_fk_2`
  FOREIGN KEY (`MatchSpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 4420
  DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`SpectrumProperty`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`SpectrumProperty` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `SpectrumId` BIGINT(20) UNSIGNED NOT NULL,
  `Name` VARCHAR(60) NOT NULL,
  `Value` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`Id`),
  INDEX `SpectrumProperty_Name_index` (`Name` ASC),
  INDEX `SpectrumProperty_Spectrum_Id_fk` (`SpectrumId` ASC),
  CONSTRAINT `SpectrumProperty_Spectrum_Id_fk`
  FOREIGN KEY (`SpectrumId`)
  REFERENCES `adapcompounddb`.`Spectrum` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 15478
  DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `adapcompounddb`.`UserParameter`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`UserParameter` (
  `Id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `UserPrincipalId` BIGINT(20) UNSIGNED NULL DEFAULT NULL,
  `Identifier` VARCHAR(200) NOT NULL,
  `Value` TEXT NOT NULL,
  `Type` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `UserParameter_UserPrincipalId_Identifier_index` (`UserPrincipalId` ASC, `Identifier` ASC),
  CONSTRAINT `UserParameter_UserPrincipal_Id_fk`
  FOREIGN KEY (`UserPrincipalId`)
  REFERENCES `adapcompounddb`.`UserPrincipal` (`Id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
  ENGINE = InnoDB
  AUTO_INCREMENT = 23
  DEFAULT CHARACTER SET = latin1;

USE `adapcompounddb` ;

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`clusterspectrumpeakview`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`clusterspectrumpeakview` (`Id` INT, `Mz` INT, `Intensity` INT, `SpectrumId` INT, `SubmissionId` INT, `Precursor` INT, `RetentionTime` INT, `ChromatographyType` INT);

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`peakview`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`peakview` (`Id` INT, `Mz` INT, `Intensity` INT, `SpectrumId` INT, `SubmissionId` INT, `Consensus` INT, `Searchable` INT, `Precursor` INT, `RetentionTime` INT, `ChromatographyType` INT);

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`searchablespectrumpeakview`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`searchablespectrumpeakview` (`Id` INT, `Mz` INT, `Intensity` INT, `SpectrumId` INT, `SubmissionId` INT, `Consensus` INT, `Precursor` INT, `RetentionTime` INT, `ChromatographyType` INT);

-- -----------------------------------------------------
-- Placeholder table for view `adapcompounddb`.`searchspectrumpeakview`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `adapcompounddb`.`searchspectrumpeakview` (`Id` INT, `Mz` INT, `Intensity` INT, `SpectrumId` INT, `SubmissionId` INT, `Precursor` INT, `RetentionTime` INT, `ChromatographyType` INT);

-- -----------------------------------------------------
-- View `adapcompounddb`.`clusterspectrumpeakview`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`clusterspectrumpeakview`;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `adapcompounddb`.`clusterspectrumpeakview` AS select `adapcompounddb`.`peak`.`Id` AS `Id`,`adapcompounddb`.`peak`.`Mz` AS `Mz`,`adapcompounddb`.`peak`.`Intensity` AS `Intensity`,`adapcompounddb`.`peak`.`SpectrumId` AS `SpectrumId`,`adapcompounddb`.`spectrum`.`SubmissionId` AS `SubmissionId`,`adapcompounddb`.`spectrum`.`Precursor` AS `Precursor`,`adapcompounddb`.`spectrum`.`RetentionTime` AS `RetentionTime`,`adapcompounddb`.`spectrum`.`ChromatographyType` AS `ChromatographyType` from (`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`) where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and (`adapcompounddb`.`spectrum`.`Consensus` is false) and (`adapcompounddb`.`spectrum`.`Reference` is false));

-- -----------------------------------------------------
-- View `adapcompounddb`.`peakview`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`peakview`;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `adapcompounddb`.`peakview` AS select `adapcompounddb`.`peak`.`Id` AS `Id`,`adapcompounddb`.`peak`.`Mz` AS `Mz`,`adapcompounddb`.`peak`.`Intensity` AS `Intensity`,`adapcompounddb`.`peak`.`SpectrumId` AS `SpectrumId`,`adapcompounddb`.`spectrum`.`SubmissionId` AS `SubmissionId`,`adapcompounddb`.`spectrum`.`Consensus` AS `Consensus`,`adapcompounddb`.`spectrum`.`Searchable` AS `Searchable`,`adapcompounddb`.`spectrum`.`Precursor` AS `Precursor`,`adapcompounddb`.`spectrum`.`RetentionTime` AS `RetentionTime`,`adapcompounddb`.`submission`.`ChromatographyType` AS `ChromatographyType` from ((`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`) join `adapcompounddb`.`submission`) where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and (`adapcompounddb`.`spectrum`.`SubmissionId` = `adapcompounddb`.`submission`.`Id`));

-- -----------------------------------------------------
-- View `adapcompounddb`.`searchablespectrumpeakview`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`searchablespectrumpeakview`;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `adapcompounddb`.`searchablespectrumpeakview` AS select `adapcompounddb`.`peak`.`Id` AS `Id`,`adapcompounddb`.`peak`.`Mz` AS `Mz`,`adapcompounddb`.`peak`.`Intensity` AS `Intensity`,`adapcompounddb`.`peak`.`SpectrumId` AS `SpectrumId`,`adapcompounddb`.`spectrum`.`SubmissionId` AS `SubmissionId`,`adapcompounddb`.`spectrum`.`Consensus` AS `Consensus`,`adapcompounddb`.`spectrum`.`Precursor` AS `Precursor`,`adapcompounddb`.`spectrum`.`RetentionTime` AS `RetentionTime`,`adapcompounddb`.`spectrumcluster`.`ChromatographyType` AS `ChromatographyType` from ((`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`) join `adapcompounddb`.`spectrumcluster`) where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and (`adapcompounddb`.`spectrum`.`ClusterId` = `adapcompounddb`.`spectrumcluster`.`Id`) and (`adapcompounddb`.`spectrum`.`Searchable` is true));

-- -----------------------------------------------------
-- View `adapcompounddb`.`searchspectrumpeakview`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `adapcompounddb`.`searchspectrumpeakview`;
USE `adapcompounddb`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `adapcompounddb`.`searchspectrumpeakview` AS select `adapcompounddb`.`peak`.`Id` AS `Id`,`adapcompounddb`.`peak`.`Mz` AS `Mz`,`adapcompounddb`.`peak`.`Intensity` AS `Intensity`,`adapcompounddb`.`peak`.`SpectrumId` AS `SpectrumId`,`adapcompounddb`.`spectrum`.`SubmissionId` AS `SubmissionId`,`adapcompounddb`.`spectrum`.`Precursor` AS `Precursor`,`adapcompounddb`.`spectrum`.`RetentionTime` AS `RetentionTime`,`adapcompounddb`.`spectrum`.`ChromatographyType` AS `ChromatographyType` from (`adapcompounddb`.`peak` join `adapcompounddb`.`spectrum`) where ((`adapcompounddb`.`peak`.`SpectrumId` = `adapcompounddb`.`spectrum`.`Id`) and ((`adapcompounddb`.`spectrum`.`Consensus` is true) or (`adapcompounddb`.`spectrum`.`Reference` is true)));

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
