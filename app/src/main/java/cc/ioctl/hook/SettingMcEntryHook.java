/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 qwq233@qwq2333.top
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */
package cc.ioctl.hook;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static io.github.qauxv.util.Initiator.load;
import static io.github.qauxv.util.Initiator.loadClass;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cc.ioctl.util.HostInfo;
import cc.ioctl.util.HostStyledViewBuilder;
import cc.ioctl.util.LayoutHelper;
import cc.ioctl.util.Reflex;
import cc.ioctl.util.ui.ViewBuilder;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import io.github.qauxv.BuildConfig;
import io.github.qauxv.R;
import io.github.qauxv.activity.SettingsUiFragmentHostActivity;
import io.github.qauxv.base.annotation.FunctionHookEntry;
import io.github.qauxv.fragment.EulaFragment;
import io.github.qauxv.hook.BasePersistBackgroundHook;
import io.github.qauxv.ui.CustomDialog;
import io.github.qauxv.ui.ResUtils;
import io.github.qauxv.util.LicenseStatus;
import io.github.qauxv.util.Log;
import io.github.qauxv.util.McHookStatus;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@FunctionHookEntry
public class SettingMcEntryHook extends BasePersistBackgroundHook {

    public static final SettingMcEntryHook INSTANCE = new SettingMcEntryHook();

    private static final int BG_TYPE_SINGLE = 0;
    private static final int BG_TYPE_FIRST = 1;
    private static final int BG_TYPE_MIDDLE = 2;
    private static final int BG_TYPE_LAST = 3;

    private SettingMcEntryHook() {
    }

    @Override
    public boolean initOnce() throws Exception {
        Class<?> kQQSettingSettingActivity = loadClass("com.tencent.mobileqq.activity.QQSettingSettingActivity");
        XposedHelpers.findAndHookMethod(kQQSettingSettingActivity, "doOnCreate", Bundle.class, mAddModuleEntry);
        return true;
    }

    private final XC_MethodHook mAddModuleEntry = new XC_MethodHook(51) {
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
            try {
                final Activity activity = (Activity) param.thisObject;
                Resources res = activity.getResources();
                Class<?> itemClass;
                View itemRef = null;
                {
                    Class<?> clz = load("com/tencent/mobileqq/widget/FormSimpleItem");
                    if (clz != null) {
                        // find a candidate view field
                        for (Field f : activity.getClass().getDeclaredFields()) {
                            if (f.getType() == clz && !Modifier.isStatic(f.getModifiers())) {
                                f.setAccessible(true);
                                View v = (View) f.get(activity);
                                if (v != null && v.getParent() != null) {
                                    itemRef = v;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (itemRef == null && (itemClass = load("com/tencent/mobileqq/widget/FormCommonSingleLineItem")) != null) {
                    itemRef = (View) Reflex.getInstanceObjectOrNull(activity, "a", itemClass);
                }
                if (itemRef == null) {
                    Class<?> clz = load("com/tencent/mobileqq/widget/FormCommonSingleLineItem");
                    if (clz == null) {
                        clz = load("com/tencent/mobileqq/widget/FormSimpleItem");
                    }
                    itemRef = (View) Reflex.getFirstNSFByType(activity, clz);
                }
                View item;
                if (itemRef == null) {
                    // we are in triassic period?
                    item = (View) Reflex.newInstance(load("com/tencent/mobileqq/widget/FormSimpleItem"), activity, Context.class);
                } else {
                    // modern age
                    item = (View) Reflex.newInstance(itemRef.getClass(), activity, Context.class);
                }
                item.setId(R.id.setting2Activity_settingEntryItem);
                Reflex.invokeVirtual(item, "setLeftText", "McHookTool", CharSequence.class);
                Reflex.invokeVirtual(item, "setBgType", 2, int.class);
                if (LicenseStatus.hasUserAcceptEula()) {
                    Reflex.invokeVirtual(item, "setRightText", "hook数据小工具", CharSequence.class);
                } else {
                    Reflex.invokeVirtual(item, "setRightText", "[未激活]", CharSequence.class);
                }
                item.setOnClickListener(v -> {
                    showMcSettingDialog(activity);
                });
                if (itemRef != null && !HostInfo.isQQHD()) {
                    //modern age
                    ViewGroup list = (ViewGroup) itemRef.getParent();
                    ViewGroup.LayoutParams reflp;
                    if (list.getChildCount() == 1) {
                        //junk!
                        list = (ViewGroup) list.getParent();
                        reflp = ((View) itemRef.getParent()).getLayoutParams();
                    } else {
                        reflp = itemRef.getLayoutParams();
                    }
                    ViewGroup.LayoutParams lp = null;
                    if (reflp != null) {
                        lp = new ViewGroup.LayoutParams(MATCH_PARENT, /*reflp.height*/WRAP_CONTENT);
                    }
                    int index = 0;
                    int account_switch = res.getIdentifier("account_switch", "id", list.getContext().getPackageName());
                    try {
                        if (account_switch > 0) {
                            View accountItem = (View) list.findViewById(account_switch).getParent();
                            if (accountItem != null && accountItem.getParent() != null) {
                                // fix up the parent for CHA
                                list = (ViewGroup) accountItem.getParent();
                            }
                            for (int i = 0; i < list.getChildCount(); i++) {
                                if (list.getChildAt(i) == accountItem) {
                                    index = i + 1;
                                    break;
                                }
                            }
                        }
                        if (index > list.getChildCount()) {
                            index = 0;
                        }
                    } catch (NullPointerException ignored) {
                    }
                    list.addView(item, index, lp);
                    fixBackgroundType(list, item, index);
                } else {
                    // triassic period, we have to find the ViewGroup ourselves
                    int qqsetting2_msg_notify = res.getIdentifier("qqsetting2_msg_notify", "id", activity.getPackageName());
                    if (qqsetting2_msg_notify == 0) {
                        throw new UnsupportedOperationException("R.id.qqsetting2_msg_notify not found in triassic period");
                    } else {
                        ViewGroup vg = (ViewGroup) activity.findViewById(qqsetting2_msg_notify).getParent().getParent();
                        vg.addView(item, 0, new ViewGroup.LayoutParams(MATCH_PARENT, /*reflp.height*/WRAP_CONTENT));
                    }
                }
            } catch (Throwable e) {
                traceError(e);
                throw e;
            }
        }
    };

    private void fixBackgroundType(@NonNull ViewGroup parent, @NonNull View itemView, int index) {
        int lastClusterId = index - 1;
        if (lastClusterId < 0) {
            // unexpected
            return;
        }
        // make QQ 8.8.80 happy
        try {
            Reflex.invokeVirtual(itemView, "setBgType", 0, int.class);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) itemView.getLayoutParams();
            lp.setMargins(0, LayoutHelper.dip2px(parent.getContext(), 15), 0, 0);
            parent.requestLayout();
        } catch (ReflectiveOperationException e) {
            Log.e(e);
        }
    }

    public void showMcSettingDialog(Context activity) {
        XposedBridge.log("[mcHookTool] -> 开始" + (activity == null ? "t" : "f"));
        CustomDialog dialog = CustomDialog.createFailsafe(activity);
        Context ctx = dialog.getContext();
        EditText editText = new EditText(ctx);
        editText.setTextSize(16f);
        int _5 = LayoutHelper.dip2px(activity, 5f);
        editText.setPadding(_5, _5, _5, _5 * 2);
        String address = McHookStatus.getServerUrl("http://192.168.8.58/hook");
        editText.setText(address);
        CheckBox checkBox = new CheckBox(ctx);
        checkBox.setText("开启数据发送");
        checkBox.setChecked(McHookStatus.getOpenStatus() == 1);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    McHookStatus.setOpenStatus(isChecked ? 1 : 0);
                    Toast.makeText(activity, "保存成功,请重启QQ", Toast.LENGTH_SHORT).show();
                }
        );
        CheckBox checkBoxCrypt = new CheckBox(ctx);
        checkBoxCrypt.setText("开启加解密包发送");
        checkBoxCrypt.setChecked(McHookStatus.getCryptStatus() == 1);
        checkBoxCrypt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    McHookStatus.setCryptStatus(isChecked ? 1 : 0);
                    Toast.makeText(activity, "保存成功,请重启QQ", Toast.LENGTH_SHORT).show();
                }
        );
        CheckBox checkBoxMd5 = new CheckBox(ctx);
        checkBoxMd5.setText("开启MD5参数发送");
        checkBoxMd5.setChecked(McHookStatus.getMd5Status() == 1);
        checkBoxMd5.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    McHookStatus.setMd5Status(isChecked ? 1 : 0);
                    Toast.makeText(activity, "保存成功,请重启QQ", Toast.LENGTH_SHORT).show();
                }
        );
        CheckBox checkBoxGuid = new CheckBox(ctx);
        checkBoxGuid.setText("开启随机guid");
        checkBoxGuid.setChecked(McHookStatus.getGuidStatus() == 1);
        checkBoxGuid.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    McHookStatus.setGuidStatus(isChecked ? 1 : 0);
                    Toast.makeText(activity, "保存成功,将在下次重启QQ生效", Toast.LENGTH_SHORT).show();
                }
        );
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(
                HostStyledViewBuilder.subtitle(activity, "本程序仅用于学习交流使用"),
                LayoutHelper.newLinearLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        _5,
                        0,
                        _5,
                        0
                )
        );
        linearLayout.addView(
                HostStyledViewBuilder.subtitle(activity, "hook发送和接受数据"),
                LayoutHelper.newLinearLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        _5,
                        0,
                        _5,
                        0
                )
        );
        linearLayout.addView(
                checkBox,
                LayoutHelper.newLinearLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        _5 * 2
                )
        );
        linearLayout.addView(
                checkBoxCrypt,
                LayoutHelper.newLinearLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        _5 * 2
                )
        );
        linearLayout.addView(
                checkBoxMd5,
                LayoutHelper.newLinearLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        _5 * 2
                )
        );
        linearLayout.addView(
                checkBoxGuid,
                LayoutHelper.newLinearLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        _5 * 2
                )
        );
        linearLayout.addView(
                editText,
                LayoutHelper.newLinearLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        _5 * 2
                )
        );
        AlertDialog alertDialog = (AlertDialog) dialog.setTitle("输入hook服务端地址")
                .setView(linearLayout)
                .setCancelable(true)
                .setPositiveButton("确认", (dialogConfirm, which) -> {
                    McHookStatus.setServerUrl(editText.getText().toString());
                    Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                }).setNeutralButton("测试连接", (dialogTest, which) -> {

                }).setNegativeButton("取消", (dialogCancel, which) -> {

                }).create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnect(editText, activity);
            }
        });
    }

    public void testConnect(EditText editText, Context activity) {
        String address = editText.getText().toString();
        if (!(address.startsWith("http") || address.startsWith("https"))) {
            Toast.makeText(activity, "请输入正确的地址", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = String.format(address + "/send?seq=%s&command=%s&uin=%s", "123", "test", "321");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(new byte[0]);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                toast(activity, "连接失败!" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                toast(activity, "连接成功");
            }
        });
    }

    public void toast(Context ctx, String text) {
        Looper.prepare();
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
