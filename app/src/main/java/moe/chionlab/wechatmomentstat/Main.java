package moe.chionlab.wechatmomentstat;

import android.os.Bundle;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        File extDir = new File(Config.EXT_DIR);
        if (!extDir.exists())
            extDir.mkdir();

        if (!lpparam.packageName.equals("com.tencent.mm"))
            return;

        findAndHookMethod("com.tencent.mm.ui.LauncherUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Hook hook = new Hook(lpparam, param);
                hook.initHook();

            }
        });

    }

}
