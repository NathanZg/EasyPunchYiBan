package com.ekertree.easypunch.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.ekertree.easypunch.pojo.User;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: PunchUtils
 * Description:
 * date: 2022/8/3 14:26
 *
 * @author Ekertree
 * @since JDK 1.8
 */
public class PunchUtils {
    public static String DRIVER_PATH;
    public static String BROWSER_LOCATION;
    public static String OCR_USER;
    public static String OCR_PASS2;
    public static String OCR_SOFT_ID;
    public static String OCR_CODE_TYPE;
    public static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Linux; Android 12; Mi 10 Build/SKQ1.211006.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/96.0.4664.104 Mobile Safari/537.36 yiban_android/5.0.11";
    public static String YI_BANG_TONG_URL = "http://f.yiban.cn/iapp610661";
    public static String LOGIN_PHONE_INPUT_XPATH = "//*[@id=\"account-txt\"]";
    public static String LOGIN_PASSWORD_INPUT_XPATH = "//*[@id=\"password-txt\"]";
    public static String LOGIN_BUTTON_XPATH = "//*[@id=\"login-btn\"]";
    public static String MORNING_PUNCH_BUTTON_XPATH ="/html/body/div[3]/a[2]";
    public static String NOON_PUNCH_BUTTON_XPATH = "/html/body/div[3]/a[3]";
    public static String[] MORNING_INPUT_IDS = {"field_1588749561_2922","field_1588749738_1026","field_1588749759_6865","field_1588749842_2715"};
    public static String[] NOON_INPUT_IDS = {"field_1588750276_2934","field_1588750304_5363","field_1588750323_2500","field_1588750343_3510"};
    public static String CODE_INPUT_ID = "yanzhengma";
    public static String AGREE_INPUT_ID = "weuiAgree";
    public static String CODE_IMG_ID = "captcha";
    public static String CONFIRM_BUTTON_XPATH = "//*[@id=\"btn\"]";
    public static String P = "/html/body/div[2]/p[1]";
    public static String VALIDATION_CODE_BUTTON_XPATH = "/html/body/div[2]/div/a";
    public static String PARSE_CODE_IMG_URL = "http://upload.chaojiying.net/Upload/Processing.php";
    public static String REPORT_CODE_ERROR_URL = "http://upload.chaojiying.net/Upload/ReportError.php";
    public static String PIC_ID;

    static {
        Setting ocrSetting = new Setting("ocr.setting");
        Setting seleniumSetting = new Setting("selenium.setting");
        DRIVER_PATH = seleniumSetting.get("driverPath");
        BROWSER_LOCATION = seleniumSetting.get("browserLocation");
        OCR_USER = ocrSetting.get("ocrUser");
        OCR_PASS2 = ocrSetting.get("ocrPass2");
        OCR_SOFT_ID = ocrSetting.get("ocrSoftId");
        OCR_CODE_TYPE = ocrSetting.get("ocrCodeType");
    }

    public static WebDriver init() {
        //设置驱动位置
        System.setProperty("webdriver.chrome.driver", DRIVER_PATH);

        ChromeOptions options = new ChromeOptions();

        //设置浏览器位置
        options.setBinary(BROWSER_LOCATION);

        //无痕
        options.addArguments("--incognito");
        //禁用GPU加速
        options.addArguments("--disable-gpu");
        //禁用3D软件光栅化器
        options.addArguments("--disable-software-rasterizer");
        //禁用插件加载
        options.addArguments("--disable-extensions");
        //关闭沙盒
        options.addArguments("--no-sandbox");
        //忽略证书错误
        options.addArguments("--ignore-certificate-errors");
        //不用/dev/shm共享内存
        options.addArguments("--disable-dev-shm-usage");
        //无窗口模式
        options.addArguments("--headless");

        //linux要设置远程调试端口 不设置会报错！
        options.addArguments("--remote-debugging-port=9230");

        ChromeDriver chrome = new ChromeDriver(options);

        return chrome;
    }

    public static WebDriver initAndSetUserInfo(User user) {
        //设置驱动位置
        System.setProperty("webdriver.chrome.driver", DRIVER_PATH);

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> deviceMetrics = new HashMap<>();
        //设置屏幕大小、像素
        deviceMetrics.put("width", 480);
        deviceMetrics.put("height", 720);
        deviceMetrics.put("pixelRatio", 3.0);

        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);

        //设置要模拟的手机标识
        if (!StrUtil.isEmpty(user.getUserAgent())) {
            mobileEmulation.put("userAgent", user.getUserAgent());
        }else{
            mobileEmulation.put("userAgent", DEFAULT_USER_AGENT);
        }

        //设置浏览器位置
        options.setBinary(BROWSER_LOCATION);

        //设置模拟手机信息
        options.setExperimentalOption("mobileEmulation", mobileEmulation);

        //无痕
        options.addArguments("--incognito");
        //禁用GPU加速
        options.addArguments("--disable-gpu");
        //禁用3D软件光栅化器
        options.addArguments("--disable-software-rasterizer");
        //禁用插件加载
        options.addArguments("--disable-extensions");
        //关闭沙盒
        options.addArguments("--no-sandbox");
        //忽略证书错误
        options.addArguments("--ignore-certificate-errors");
        //不用/dev/shm共享内存
        options.addArguments("--disable-dev-shm-usage");
        //无窗口模式
        options.addArguments("--headless");

        //linux要设置远程调试端口 不设置会报错！
        options.addArguments("--remote-debugging-port=9230");

        ChromeDriver chrome = new ChromeDriver(options);

        return chrome;
    }

    //获取登陆手机输入框
    public static WebElement getLoginPhoneInput(WebDriver driver) {
        return findElementByXpath(driver, LOGIN_PHONE_INPUT_XPATH);
    }

    //获取登陆密码输入框
    public static WebElement getLoginPasswordInput(WebDriver driver) {
        return findElementByXpath(driver, LOGIN_PASSWORD_INPUT_XPATH);
    }

    //获取登陆按钮
    public static WebElement getLoginButton(WebDriver driver) {
        return findElementByXpath(driver, LOGIN_BUTTON_XPATH);
    }

    //移除只读限制
    public static void removeReadonly(WebDriver driver, int type) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
        if (PunchType.MORNING == type) {
            for (String id : MORNING_INPUT_IDS) {
                jsExecutor.executeScript("document.getElementById('"+id+"').removeAttribute('readonly')");
            }
        }else if (PunchType.NOON == type) {
            for (String id : NOON_INPUT_IDS) {
                jsExecutor.executeScript("document.getElementById('" + id + "').removeAttribute('readonly')");
            }
        }
    }

    //移除定位
    public static void removePositioning(WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
        jsExecutor.executeScript("document.getElementsByClassName('weui-mask_transparent')[0].style.display='none'");
        jsExecutor.executeScript("document.getElementsByClassName('weui-toast weui_loading_toast weui-toast--visible')[0].style.display='none'");
    }

    //根据id查找元素
    public static WebElement findElementById(WebDriver driver,String id) {
        return driver.findElement(By.id(id));
    }

    //根据xpath查找元素
    public static WebElement findElementByXpath(WebDriver driver,String xpath){
        return driver.findElement(By.xpath(xpath));
    }

    //获取base64验证码
    public static String getCodeBase64Img(WebDriver driver) {
        WebElement codeImg = findElementById(driver, CODE_IMG_ID);
        return codeImg.getAttribute("src");
    }

    //解析验证码
    public static String parseCodeImg(WebDriver driver) {
        String codeBase64Img = getCodeBase64Img(driver);
        String[] splits = codeBase64Img.split(",");
        codeBase64Img = splits[1];
        HashMap<String, Object> params = new HashMap<>();
        params.put("user", OCR_USER);
        params.put("pass2", OCR_PASS2);
        params.put("softid", OCR_SOFT_ID);
        params.put("codetype", OCR_CODE_TYPE);
        params.put("file_base64", codeBase64Img);
        HttpResponse response = HttpRequest.post(PARSE_CODE_IMG_URL).form(params).execute();
        JSONObject responseJson = JSONUtil.parseObj(response.body());
        Integer err_no = (Integer) responseJson.get("err_no");
        PIC_ID = (String) responseJson.get("pic_id");
        if (err_no == 0) {
            return (String) responseJson.get("pic_str");
        }else{
            ReportCodeError();
            return "";
        }
    }

    public static void ReportCodeError() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user", OCR_USER);
        params.put("pass2", OCR_PASS2);
        params.put("softid", OCR_SOFT_ID);
        params.put("id", PIC_ID);
        HttpRequest.post(REPORT_CODE_ERROR_URL).form(params).execute();
    }

    //获得验证码输入框
    public static WebElement getCodeInput(WebDriver driver) {
        return findElementById(driver, CODE_INPUT_ID);
    }

    //勾选同意须知
    public static void agree(WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
        ((JavascriptExecutor) driver).executeScript("document.getElementById('"+AGREE_INPUT_ID+"').value = '1'");
    }

    //获取提交按钮
    public static WebElement getConfirmButton(WebDriver driver) {
        return findElementByXpath(driver, CONFIRM_BUTTON_XPATH);
    }

    //获取验证二维码按钮
    public static  WebElement getValidationCodeButton(WebDriver driver) {
        return findElementByXpath(driver, VALIDATION_CODE_BUTTON_XPATH);
    }

    //截图
    public static void screenShotAndSendEmail(WebDriver driver,User user,int type) {
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File screenshot = takesScreenshot.getScreenshotAs(OutputType.FILE);
        if (PunchType.MORNING == type) {
            EmailUtils.sendMessage(user,"大爷，晨检打卡成功！", screenshot);
        }else if (PunchType.NOON == type) {
            EmailUtils.sendMessage(user,"大爷，午检打卡成功！", screenshot);
        }
    }
}
