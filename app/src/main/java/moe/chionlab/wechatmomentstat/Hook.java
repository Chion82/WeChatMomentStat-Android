package moe.chionlab.wechatmomentstat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by chiontang on 2/11/16.
 */
public class Hook {

    Class SnsDetail = null;
    Class SnsDetailParser = null;
    Class SnsObject = null;

    Context wechatContext = null;

    XC_LoadPackage.LoadPackageParam lpparam = null;
    XC_MethodHook.MethodHookParam param = null;

    SnsReader bgTask = null;

    Hook(XC_LoadPackage.LoadPackageParam lpparam, XC_MethodHook.MethodHookParam param) {
        this.lpparam = lpparam;
        this.param = param;
    }

    public void initHook() throws Throwable {
        initWeChatVersion(param.thisObject, "com.tencent.mm");
        if (!Config.ready) {
            XposedBridge.log("Unsupported WeChat version.");
            return;
        }
        getClasses(lpparam.classLoader);
        SnsReader snsReader = new SnsReader(SnsDetail, SnsDetailParser, SnsObject);
        snsReader.run();
    }

    protected void initWeChatVersion(Object wechatActivity, String packageName) throws Throwable{
        XposedBridge.log("LauncherUI hooked.");
        Context appContext = ((Activity)wechatActivity).getApplicationContext();
        wechatContext = appContext;
        PackageInfo pInfo = appContext.getPackageManager().getPackageInfo(packageName, 0);
        String wechatVersion = "";
        if (pInfo != null)
            wechatVersion = pInfo.versionName;
        XposedBridge.log("WeChat version=" + wechatVersion);
        Config.initWeChatVersion(wechatVersion);
    }

    protected void getClasses(ClassLoader classLoader) throws Throwable {
        SnsDetailParser = XposedHelpers.findClass(Config.SNS_XML_GENERATOR_CLASS, classLoader);
        SnsDetail = XposedHelpers.findClass(Config.PROTOCAL_SNS_DETAIL_CLASS, classLoader);
        SnsObject = XposedHelpers.findClass(Config.PROTOCAL_SNS_OBJECT_CLASS, classLoader);
    }

}
