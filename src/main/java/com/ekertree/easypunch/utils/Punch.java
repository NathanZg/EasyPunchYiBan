package com.ekertree.easypunch.utils;

import cn.hutool.core.util.StrUtil;
import com.ekertree.easypunch.pojo.User;
import com.ekertree.easypunch.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


/**
 * ClassName: Punch
 * Description:
 * date: 2022/8/3 14:55
 *
 * @author Ekertree
 * @since JDK 1.8
 */
@Slf4j
public class Punch {

    public void doMorningPunch() {
        log.info("定时任务【晨检打卡】开始执行......");
        List<User> userList = UserService.getMorningPunchUserList();
        if (userList.size() > 0) {
            log.info("将为" + userList.size() + "名用户进行晨检打卡......");
            userList.forEach(user -> {
                WebDriver driver = PunchUtils.initAndSetUserInfo(user);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20), Duration.ofSeconds(1));
                if (!StrUtil.isEmpty(user.getSessionUrl())) {
                    driver.get(EncryptUtils.decode(user.getSessionUrl()));
                } else {
                    driver.quit();
                    if (user.getIfSendEmail() == 1) {
                        EmailUtils.sendMessage(user, "晨检打卡失败，原因是session_url为空，请进行更新！");
                    }
                    log.error("[" + user.getPhone() + "] {session_url为空！}");
                    return;
                }
                try {
                    wait.until(dr -> dr.findElement(By.xpath(PunchUtils.MORNING_PUNCH_BUTTON_XPATH))).click();
                    try {
                        WebElement P = wait.until(dr -> dr.findElement(By.xpath(PunchUtils.P)));
                        if (P.getText().contains("提交成功")) {
                            if (user.getIfSendEmail() == 1) {
                                PunchUtils.screenShotAndSendEmail(driver, user, PunchType.MORNING);
                            }
                            driver.quit();
                            user.setMorning(1);
                            UserService.updateById(user);
                            log.info("[" + user.getPhone() + "] {晨检打卡成功！}");
                            return;
                        } else if (P.getText().contains("时间不正确")) {
                            driver.quit();
                            if (user.getIfSendEmail() == 1) {
                                EmailUtils.sendMessage(user, "晨检打卡失败,原因是未到打卡时间。");
                            }
                            log.error("[" + user.getPhone() + "]{晨检打卡失败,原因是未到打卡时间。}");
                            return;
                        }
                    } catch (Exception e) {
                        try {
                            wait.until(dr -> dr.findElement(By.id(PunchUtils.CODE_IMG_ID)));
                            PunchUtils.removePositioning(driver);
                            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
                            PunchUtils.removeReadonly(driver, PunchType.MORNING);
                            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
                            WebElement templateInput = PunchUtils.findElementById(driver, PunchUtils.MORNING_INPUT_IDS[0]);
                            templateInput.sendKeys("36.3");
                            WebElement locationInput = PunchUtils.findElementById(driver, PunchUtils.MORNING_INPUT_IDS[1]);
                            locationInput.sendKeys(user.getAddress());
                            WebElement isInDormitory = PunchUtils.findElementById(driver, PunchUtils.MORNING_INPUT_IDS[2]);
                            isInDormitory.sendKeys("是");
                            WebElement isGetSymptoms = PunchUtils.findElementById(driver, PunchUtils.MORNING_INPUT_IDS[3]);
                            isGetSymptoms.sendKeys("否");
                            WebElement codeInput = PunchUtils.getCodeInput(driver);
                            codeInput.sendKeys(PunchUtils.parseCodeImg(driver));
                            PunchUtils.agree(driver);
                            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
                            String code = PunchUtils.parseCodeImg(driver);
                            if (!StrUtil.isEmpty(code)) {
                                codeInput.sendKeys(code);
                                WebElement confirmButton = PunchUtils.getConfirmButton(driver);
                                confirmButton.click();
                                WebElement P = null;
                                try {
                                    P = wait.until(dr -> dr.findElement(By.xpath(PunchUtils.P)));
                                    if (P.getText().contains("提交成功")) {
                                        if (user.getIfSendEmail() == 1) {
                                            PunchUtils.screenShotAndSendEmail(driver, user, PunchType.MORNING);
                                        }
                                        driver.quit();
                                        user.setMorning(1);
                                        UserService.updateById(user);
                                        log.info("[" + user.getPhone() + "] {晨检打卡成功!}");
                                        return;
                                    } else if (P.getText().contains("时间不正确")) {
                                        driver.quit();
                                        if (user.getIfSendEmail() == 1) {
                                            EmailUtils.sendMessage(user, "晨检打卡失败，原因是提交后刚好到截至时间。");
                                        }
                                        log.error("[" + user.getPhone() + "] {晨检打卡失败，原因是提交后刚好到截至时间。}");
                                        return;
                                    }
                                } catch (Exception ex) {
                                    try {
                                        driver.findElement(By.id(PunchUtils.CODE_INPUT_ID));
                                        driver.quit();
                                        if (user.getIfSendEmail() == 1) {
                                            EmailUtils.sendMessage(user, "晨检打卡失败，原因是验证码识别错误!");
                                        }
                                        log.error("[" + user.getPhone() + "] {验证码识别错误!}");
                                        return;
                                    } catch (Exception exc) {
                                        driver.quit();
                                        if (user.getIfSendEmail() == 1) {
                                            EmailUtils.sendMessage(user, "晨检打卡失败，未知原因导致晨检打卡失败!");
                                        }
                                        log.error("[" + user.getPhone() + "] {未知原因导致晨检打卡失败!}");
                                        return;
                                    }
                                }
                            } else {
                                driver.quit();
                                if (user.getIfSendEmail() == 1) {
                                    EmailUtils.sendMessage(user, "晨检打卡失败，原因是验证码解析失败！");
                                }
                                log.error("[" + user.getPhone() + "] {验证码解析失败!}");
                                return;
                            }
                        } catch (Exception ex) {
                            driver.quit();
                            if (user.getIfSendEmail() == 1) {
                                EmailUtils.sendMessage(user, "晨检打卡失败，原因是表单页面加载失败！");
                            }
                            log.info("晨检打卡失败，原因是表单页面加载失败！");
                        }
                    }
                } catch (Exception e) {
                    driver.quit();
                    if (user.getIfSendEmail() == 1) {
                        EmailUtils.sendMessage(user, "晨检打卡失败，原因是session_url失效或者页面加载失败!");
                    }
                    log.error("[" + user.getPhone() + "] {session_url失效或者页面加载失败!}");
                    return;
                }
            });
        }
        log.info("定时任务【晨检打卡】执行完毕......");
    }

    public void doNoonPunch() {
        log.info("定时任务【午检打卡】开始执行......");
        List<User> userList = UserService.getNoonPunchUserList();
        if (userList.size() > 0) {
            log.info("将为" + userList.size() + "名用户进行午检打卡......");
            userList.forEach(user -> {
                WebDriver driver = PunchUtils.initAndSetUserInfo(user);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30), Duration.ofSeconds(1));
                if (!StrUtil.isEmpty(user.getSessionUrl())) {
                    driver.get(EncryptUtils.decode(user.getSessionUrl()));
                } else {
                    driver.quit();
                    if (user.getIfSendEmail() == 1) {
                        EmailUtils.sendMessage(user, "午检打卡失败，原因是session_url为空，请进行更新！");
                    }
                    log.error("[" + user.getPhone() + "] {session_url为空！}");
                    return;
                }
                try {
                    wait.until(dr -> dr.findElement(By.xpath(PunchUtils.NOON_PUNCH_BUTTON_XPATH))).click();
                    try {
                        WebElement P = wait.until(dr -> dr.findElement(By.xpath(PunchUtils.P)));
                        if (P.getText().contains("提交成功")) {
                            if (user.getIfSendEmail() == 1) {
                                PunchUtils.screenShotAndSendEmail(driver, user, PunchType.NOON);
                            }
                            driver.quit();
                            user.setNoon(1);
                            UserService.updateById(user);
                            log.info("[" + user.getPhone() + "] {午检打卡成功！}");
                            return;
                        } else if (P.getText().contains("时间不正确")) {
                            driver.quit();
                            if (user.getIfSendEmail() == 1) {
                                EmailUtils.sendMessage(user, "午检打卡失败,原因是未到打卡时间。");
                            }
                            log.error("[" + user.getPhone() + "]{午检打卡失败,原因是未到打卡时间。}");
                            return;
                        }
                    } catch (Exception e) {
                        try {
                            wait.until(dr -> dr.findElement(By.id(PunchUtils.CODE_IMG_ID)));
                            PunchUtils.removePositioning(driver);
                            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
                            PunchUtils.removeReadonly(driver, PunchType.NOON);
                            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
                            WebElement templateInput = PunchUtils.findElementById(driver, PunchUtils.NOON_INPUT_IDS[0]);
                            templateInput.sendKeys("36.3");
                            WebElement locationInput = PunchUtils.findElementById(driver, PunchUtils.NOON_INPUT_IDS[1]);
                            locationInput.sendKeys(user.getAddress());
                            WebElement isInDormitory = PunchUtils.findElementById(driver, PunchUtils.NOON_INPUT_IDS[2]);
                            isInDormitory.sendKeys("是");
                            WebElement isGetSymptoms = PunchUtils.findElementById(driver, PunchUtils.NOON_INPUT_IDS[3]);
                            isGetSymptoms.sendKeys("否");
                            WebElement codeInput = PunchUtils.getCodeInput(driver);
                            codeInput.sendKeys(PunchUtils.parseCodeImg(driver));
                            PunchUtils.agree(driver);
                            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
                            String code = PunchUtils.parseCodeImg(driver);
                            if (!StrUtil.isEmpty(code)) {
                                codeInput.sendKeys(code);
                                WebElement confirmButton = PunchUtils.getConfirmButton(driver);
                                confirmButton.click();
                                WebElement P = null;
                                try {
                                    P = wait.until(dr -> dr.findElement(By.xpath(PunchUtils.P)));
                                    if (P.getText().contains("提交成功")) {
                                        if (user.getIfSendEmail() == 1) {
                                            PunchUtils.screenShotAndSendEmail(driver, user, PunchType.NOON);
                                        }
                                        driver.quit();
                                        user.setNoon(1);
                                        UserService.updateById(user);
                                        log.info("[" + user.getPhone() + "] {午检打卡成功!}");
                                        return;
                                    } else if (P.getText().contains("时间不正确")) {
                                        driver.quit();
                                        if (user.getIfSendEmail() == 1) {
                                            EmailUtils.sendMessage(user, "午检打卡失败，原因是提交后刚好到截至时间。");
                                        }
                                        log.error("[" + user.getPhone() + "] {午检打卡失败，原因是提交后刚好到截至时间。}");
                                        return;
                                    }
                                } catch (Exception ex) {
                                    try {
                                        driver.findElement(By.id(PunchUtils.CODE_INPUT_ID));
                                        driver.quit();
                                        if (user.getIfSendEmail() == 1) {
                                            EmailUtils.sendMessage(user, "午检打卡失败，原因是验证码识别错误!");
                                        }
                                        log.error("[" + user.getPhone() + "] {验证码识别错误!}");
                                        return;
                                    } catch (Exception exc) {
                                        driver.quit();
                                        if (user.getIfSendEmail() == 1) {
                                            EmailUtils.sendMessage(user, "午检打卡失败，未知原因导致午检打卡失败!");
                                        }
                                        log.error("[" + user.getPhone() + "] {未知原因导致午检打卡失败!}");
                                        return;
                                    }
                                }
                            } else {
                                driver.quit();
                                if (user.getIfSendEmail() == 1) {
                                    EmailUtils.sendMessage(user, "午检打卡失败，原因是验证码解析失败！");
                                }
                                log.error("[" + user.getPhone() + "] {验证码解析失败!}");
                                return;
                            }
                        } catch (Exception ex) {
                            driver.quit();
                            if (user.getIfSendEmail() == 1) {
                                EmailUtils.sendMessage(user, "午检打卡失败，原因是表单页面加载失败！");
                            }
                            log.info("午检打卡失败，原因是表单页面加载失败！");
                        }
                    }
                } catch (Exception e) {
                    driver.quit();
                    if (user.getIfSendEmail() == 1) {
                        EmailUtils.sendMessage(user, "午检打卡失败，原因是session_url失效或者页面加载失败!");
                    }
                    log.error("[" + user.getPhone() + "] {session_url失效或者页面加载失败!}");
                    return;
                }
            });
        }
        log.info("定时任务【午检打卡】执行完毕......");
    }

    public void updatePunchUserListSessionUrl() {
        log.info("定时任务【更新SessionUrl】开始执行......");
        List<User> userList = UserService.getPunchUserList();
        if (userList.size() > 0) {
            log.info("将为" + userList.size() + "名用户进行更新SessionUrl......");
            WebDriver driver = PunchUtils.init();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20), Duration.ofSeconds(1));
            userList.forEach(user -> {
                driver.manage().deleteAllCookies();
                driver.get(PunchUtils.YI_BANG_TONG_URL);
                try {
                    wait.until(dr -> dr.findElement(By.xpath(PunchUtils.LOGIN_BUTTON_XPATH)));
                    WebElement loginPhoneInput = PunchUtils.getLoginPhoneInput(driver);
                    loginPhoneInput.sendKeys(user.getPhone());
                    WebElement loginPasswordInput = PunchUtils.getLoginPasswordInput(driver);
                    loginPasswordInput.sendKeys(EncryptUtils.decode(user.getPassword()));
                    WebElement loginButton = PunchUtils.getLoginButton(driver);
                    loginButton.click();
                    try {
                        wait.until(dr -> dr.findElement(By.xpath(PunchUtils.MORNING_PUNCH_BUTTON_XPATH)));
                        String currentUrl = driver.getCurrentUrl();
                        if (!StrUtil.isEmpty(currentUrl)) {
                            user.setSessionUrl(EncryptUtils.encode(currentUrl));
                            UserService.updateById(user);
                            if (user.getIfSendEmail() == 1) {
                                EmailUtils.sendMessage(user, "sessionUrl更新成功！");
                            }
                            log.info("[" + user.getPhone() + "] {sessionUrl更新成功！}");
                            return;
                        } else {
                            if (user.getIfSendEmail() == 1) {
                                EmailUtils.sendMessage(user, "获取的sessionUrl为空，更新失败！");
                            }
                            log.error("[" + user.getPhone() + "] {获取的sessionUrl为空，更新失败！}");
                            return;
                        }
                    } catch (Exception e) {
                        if (user.getIfSendEmail() == 1) {
                            EmailUtils.sendMessage(user, "未知原因加载打卡页面失败或存入数据库失败，更新sessionUrl失败！");
                        }
                        log.error("[" + user.getPhone() + "] {未知原因加载打卡页面失败或存入数据库失败，更新sessionUrl失败！}");
                        return;
                    }
                } catch (Exception e) {
                    if (user.getIfSendEmail() == 1) {
                        EmailUtils.sendMessage(user, "更新sessionUrl失败，原因是登陆页面加载失败！");
                    }
                    log.info("更新sessionUrl失败，原因是登陆页面加载失败！");
                    return;
                }
            });
            driver.quit();
        }
        log.info("定时任务【更新SessionUrl】执行完毕......");
    }

    public void updateUserSessionUrl(User user) {
        log.info("将为用户" + user.getPhone() + "更新sessionUrl......");
        WebDriver driver = PunchUtils.init();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20), Duration.ofSeconds(1));
        driver.manage().deleteAllCookies();
        driver.get(PunchUtils.YI_BANG_TONG_URL);
        try {
            wait.until(dr -> dr.findElement(By.xpath(PunchUtils.LOGIN_BUTTON_XPATH)));
            WebElement loginPhoneInput = PunchUtils.getLoginPhoneInput(driver);
            loginPhoneInput.sendKeys(user.getPhone());
            WebElement loginPasswordInput = PunchUtils.getLoginPasswordInput(driver);
            loginPasswordInput.sendKeys(EncryptUtils.decode(user.getPassword()));
            WebElement loginButton = PunchUtils.getLoginButton(driver);
            loginButton.click();
            try {
                wait.until(dr -> dr.findElement(By.xpath(PunchUtils.MORNING_PUNCH_BUTTON_XPATH)));
                String currentUrl = driver.getCurrentUrl();
                if (!StrUtil.isEmpty(currentUrl)) {
                    user.setSessionUrl(EncryptUtils.encode(currentUrl));
                    UserService.updateById(user);
                    if (user.getIfSendEmail() == 1) {
                        EmailUtils.sendMessage(user, "sessionUrl更新成功！");
                    }
                    log.info("[" + user.getPhone() + "] {sessionUrl更新成功！}");
                    log.info("为用户"+user.getPhone()+"更新sessionUrl执行完毕......");
                    return;
                } else {
                    if (user.getIfSendEmail() == 1) {
                        EmailUtils.sendMessage(user, "获取的sessionUrl为空，更新失败！");
                    }
                    log.error("[" + user.getPhone() + "] {获取的sessionUrl为空，更新失败！}");
                    return;
                }
            } catch (Exception e) {
                if (user.getIfSendEmail() == 1) {
                    EmailUtils.sendMessage(user, "未知原因加载打卡页面失败或存入数据库失败，更新sessionUrl失败！");
                }
                log.error("[" + user.getPhone() + "] {未知原因加载打卡页面失败或存入数据库失败，更新sessionUrl失败！}");
                return;
            }
        } catch (Exception e) {
            if (user.getIfSendEmail() == 1) {
                EmailUtils.sendMessage(user, "更新sessionUrl失败，原因是登陆页面加载失败！");
            }
            log.info("更新sessionUrl失败，原因是登陆页面加载失败！");
            return;
        }
    }

    public void updatePunchUserListStatus() {
        log.info("定时任务【重置打卡状态】开始执行......");
        List<User> userList = UserService.getPunchUserList();
        userList.forEach(user -> {
            log.info("将为" + userList.size() + "名用户进行重置打卡状态......");
            user.setMorning(0);
            user.setNoon(0);
            UserService.updateById(user);
            log.info("[" + user.getPhone() + "] {重置打卡状态成功！}");
        });
        log.info("定时任务【重置打卡状态】执行完毕......");
    }
}
