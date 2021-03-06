package org.uiautomation.ios.e2e;

import static org.uiautomation.ios.IOSCapabilities.BUNDLE_NAME;
import static org.uiautomation.ios.IOSCapabilities.DEVICE;
import static org.uiautomation.ios.IOSCapabilities.LANGUAGE;
import static org.uiautomation.ios.IOSCapabilities.LOCALE;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.BeanToJsonConverter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.uiautomation.ios.BaseIOSDriverTest;
import org.uiautomation.ios.IOSCapabilities;
import org.uiautomation.ios.SampleApps;
import org.uiautomation.ios.UIAModels.Orientation;
import org.uiautomation.ios.client.uiamodels.impl.RemoteUIADriver;
import org.uiautomation.ios.communication.device.Device;
import org.uiautomation.ios.communication.device.DeviceVariation;
import org.uiautomation.ios.server.utils.ClassicCommands;

public class NewSessionTest extends BaseIOSDriverTest {

  @Test
  public void base() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());
      IOSCapabilities cap = IOSCapabilities.iphone("UICatalog", "2.10");
      String sdk = cap.getSDKVersion();
      if (sdk == null) {
        sdk = ClassicCommands.getDefaultSDK();
      }
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10");
      Assert.assertEquals(actual.getSDKVersion(), sdk);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void noVersion() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());

      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void startDefaultLanguageLocale() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), SampleApps.uiCatalogCap());

      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10"); // default to UK
      Assert.assertEquals(actual.getLanguage(), "en");
      Assert.assertEquals(actual.getLocale(), "en_GB");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }

  @Test
  public void startSpecifiedLanguageLocale() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      cap.setLanguage("fr");
      cap.setLocale("es");
      driver = new RemoteUIADriver(getRemoteURL(), cap);

      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.InternationalMountains");
      Assert.assertEquals(actual.getBundleVersion(), "1.1");
      // default to UK Assert.assertEquals(target.getLanguage(), "fr");
      Assert.assertEquals(actual.getLocale(), "es");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void recognizeUnsupportedLanguageLocale() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      cap.setLanguage("es");
      cap.setLocale("es");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void doesntExist() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), IOSCapabilities.iphone("ferret", "2.10"));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void sdkTooOld() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = SampleApps.uiCatalogCap();
      cap.setSDKVersion("4.3");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void wrongVersion() {
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), IOSCapabilities.iphone("UICatalog", "not a number."));
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test(expectedExceptions = SessionNotCreatedException.class)
  public void wrongSDK() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      cap.setSDKVersion("17");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void correctSDK() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
      String sdk = ClassicCommands.getDefaultSDK();
      cap.setSDKVersion(sdk);
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();

      Assert.assertEquals(actual.getSDKVersion(), sdk);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void supportAllInstalledSDKs() {
    RemoteUIADriver driver = null;
    List<String> sdks = ClassicCommands.getInstalledSDKs();
    for (String sdk : sdks) {
      Float version = Float.parseFloat(sdk);
      if (version >= 5L) {

        try {
          IOSCapabilities cap = IOSCapabilities.iphone("InternationalMountains");
          cap.setSDKVersion(sdk);

          driver = new RemoteUIADriver(getRemoteURL(), cap);
          IOSCapabilities actual = driver.getCapabilities();

          Assert.assertEquals(actual.getSDKVersion(), sdk);
        } finally {
          if (driver != null) {
            driver.quit();
          }
        }
      }
    }
  }

  @Test
  public void correctDevice() {
    RemoteUIADriver driver = null;
    try {
      IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getDevice(), Device.iphone);

    } finally {
      if (driver != null) {
        driver.quit();
      }
    }

    try {
      IOSCapabilities cap = IOSCapabilities.ipad("UICatalog");
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getDevice(), Device.ipad);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @Test
  public void canUseAnyFlagFromInfoPlistMatches() {
    IOSCapabilities cap = IOSCapabilities.iphone("UICatalog");
    cap.setCapability(IOSCapabilities.MAGIC_PREFIX + "CFBundleDevelopmentRegion", "en");
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      IOSCapabilities actual = driver.getCapabilities();
      Assert.assertEquals(actual.getBundleId(), "com.yourcompany.UICatalog");
      Assert.assertEquals(actual.getBundleVersion(), "2.10");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @DataProvider(name = "capabilities")
  public Object[][] createData1() {
    return new Object[][] {

    { Device.iphone, DeviceVariation.Regular, 320, 480 },
    { Device.iphone, DeviceVariation.Retina35, 640, 960 },
    { Device.iphone, DeviceVariation.Retina4, 640, 1136 },
    { Device.ipad, DeviceVariation.Regular, 768, 1024 },
    { Device.ipad, DeviceVariation.Retina, 1536, 2048 },

    };
  }

  @Test(dataProvider = "capabilities")
  public void supportApplicationWithMultipleDeviceFamily(Device device, DeviceVariation variation, int expectedW,
      int expectedH) throws Exception {
    IOSCapabilities cap = new IOSCapabilities();

    cap.setCapability(DEVICE, device);
    cap.setDeviceVariation(variation);

    cap.setCapability(LANGUAGE, "es");
    cap.setCapability(LOCALE, "en_GB");
    cap.setCapability(BUNDLE_NAME, "Safari");

    // normal iphone
    RemoteUIADriver driver = null;
    try {
      driver = new RemoteUIADriver(getRemoteURL(), cap);
      Capabilities actual = driver.getCapabilities();

      driver.switchTo().window("Web");
      driver.get("http://www.ebay.co.uk/");

      File tmp = new File("/Users/freynaud/Documents/tmp");
      String c = new BeanToJsonConverter().convert(actual).toString();
      FileOutputStream ou = new FileOutputStream(new File(tmp, device + "_" + variation + ".json"));
      IOUtils.write(c, ou, "UTF-8");
      IOUtils.closeQuietly(ou);
      for (Orientation o : Orientation.values()) {
        if (o == Orientation.UIA_DEVICE_ORIENTATION_FACEUP || o == Orientation.UIA_DEVICE_ORIENTATION_FACEDOWN
            || (o == Orientation.UIA_DEVICE_ORIENTATION_PORTRAIT_UPSIDEDOWN && device == Device.iphone)) {
          continue;
        }
        String name = device + "_" + variation + "_" + o;
        driver.setDeviceOrientation(o);
        JSONObject logElement = driver.logElementTree(new File(tmp, name + ".png"), true);


        FileOutputStream out = new FileOutputStream(new File(tmp, name + ".json"));
        String s = logElement.toString();
        IOUtils.write(s,out, "UTF-8");
        IOUtils.closeQuietly(out);
      }
     
      Assert.assertEquals(actual.getCapability(DEVICE),device.toString());

      // File f = ((TakesScreenshot) new
      // Augmenter().augment(driver)).getScreenshotAs(OutputType.FILE);
      /*
       * File f = driver.getScreenshotAs(OutputType.FILE);
       * 
       * BufferedImage bimg = ImageIO.read(f);
       * Assert.assertEquals(bimg.getWidth(), expectedW);
       * Assert.assertEquals(bimg.getHeight(), expectedH);
       */
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

}
