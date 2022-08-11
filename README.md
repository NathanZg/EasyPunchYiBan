# EASY PUNCH YIBAN

> Use Java, Selenium and HuTool to implement punch-in function

**<font color=red size=6>This project is for learning only, at your own risk, using this project means agreeing to the terms.</font>**

## Plan List

- [x] Punch
- [x] Generate log
- [x] Timed execution
- [x] Changeable execution time
- [x] Mail punch report
- [x] Custom key encryption password and sessionUrl
- [x] Support changing User-Agent
- [ ] Web GUI
- [ ] WeChat applet

## Usage

1. clone this repository

2. Modify the configuration file in the resources folder

   1. Modify `mail.setting` under resources/config
      1. [Fill in the rules to view the link](https://hutool.cn/docs/#/extra/%E9%82%AE%E4%BB%B6%E5%B7%A5%E5%85%B7-MailUtil)
   2. Modify `encrypt.setting` under the resources folder and enter the 16-digit key
   3. Modify  `jdbc.properties`
   4. Modify  the information of the third-party api that identifies the verification code in `ocr.setting`
      1. [Fill in the rules to view the link](https://www.chaojiying.com/)
      2. Pass2 is the md5 value of the password (32-bit lowercase)
   5. Modify `selenium.setting`
      1. `driverPath`：Browser driver storage location
      2. `browserLocation`：where the browser is installed
      3. [Click the link to view the specific tutorial](https://blog.csdn.net/hanxue6898/article/details/81184907?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522165978634816782184674849%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=165978634816782184674849&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~baidu_landing_v2~default-1-81184907-null-null.142^v39^pc_rank_34_ctr25,185^v2^control&utm_term=linux%20java%20%E4%BD%BF%E7%94%A8selenium&spm=1018.2226.3001.4187)

3. create database

   1. User database

      ```sql
      SET NAMES utf8mb4;
      SET FOREIGN_KEY_CHECKS = 0;
      
      -- ----------------------------
      -- Table structure for user
      -- ----------------------------
      DROP TABLE IF EXISTS `user`;
      CREATE TABLE `user`  (
        `phone` char(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号',
        `qq` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'qq号',
        `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
        `session_url` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '打卡地址',
        `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工作日打卡地址',
        `home_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '假期打卡地址',
        `user_agent` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机标识',
        `if_send_Email` int(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否需要发送邮件',
        `if_send_wecheat` int(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '是否需要发送微信',
        `if_punch` int(1) NOT NULL DEFAULT 1 COMMENT '是否需要自动打卡',
        `morning` int(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '晨检打卡是否成功',
        `noon` int(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '午检打卡是否成功',
        PRIMARY KEY (`phone`) USING BTREE
      ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
      
      SET FOREIGN_KEY_CHECKS = 1;
      ```

   2. task database

      ```sql
      SET NAMES utf8mb4;
      SET FOREIGN_KEY_CHECKS = 0;
      
      -- ----------------------------
      -- Table structure for task
      -- ----------------------------
      DROP TABLE IF EXISTS `task`;
      CREATE TABLE `task`  (
        `task_id` char(19) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务id',
        `task_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务名称',
        `cron` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'cron表达式',
        PRIMARY KEY (`task_id`) USING BTREE
      ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
      
      -- ----------------------------
      -- Records of task
      -- ----------------------------
      INSERT INTO `task` VALUES ('1554799269242855426', '晨检打卡', '0 0 6,7,8 * * ? *');
      INSERT INTO `task` VALUES ('1554799270140436481', '午检打卡', '0 0 12,13,14 * * ? *');
      INSERT INTO `task` VALUES ('1555028649810391041', '更新sessionUrl', '0 0 0 1/8 * ? *');
      INSERT INTO `task` VALUES ('1555178712486383617', '更新打卡状态', '0 0 0 * * ? *');
      
      SET FOREIGN_KEY_CHECKS = 1;
      ```

4. run `sudo nohup java -jar EasyPunchYiBan-1.0-SNAPSHOT.jar >/dev/null 2>&1 &` and enjoy

## Changelog

- 2022.8.6 

  > 1. Realize timing punch function and email report
  
- 2022.8.7

  > 1. Fixed the situation where the check-in status could not be recorded
  > 2. Optimize page load wait for poor network conditions
  
- 2022.8.8

  > 1. Fix error notification
  > 2. Extend the waiting time for the lunch check page
  
- 2022.8.11

  > 1.Fixed the problem that the chrome and chromedriver processes were not closed after performing the punch-in, which     	caused the memory usage and CPU usage to increase, resulting in a freeze
  > 2.Added verification code identification error report rebate function