/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2023 QAuxiliary developers
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
package cc.ioctl.hook.experimental;

import static io.github.qauxv.util.HostInfo.requireMinQQVersion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cc.ioctl.util.HookUtils;
import cc.ioctl.util.HostInfo;
import com.akashi.pb.EmosmPb$SubCmd0x2RspFetchTab$TabInfo;
import com.google.gson.Gson;
import de.robv.android.xposed.XposedBridge;
import io.github.qauxv.base.annotation.FunctionHookEntry;
import io.github.qauxv.base.annotation.UiItemAgentEntry;
import io.github.qauxv.dsl.FunctionEntryRouter;
import io.github.qauxv.hook.CommonSwitchFunctionHook;
import io.github.qauxv.util.Initiator;
import io.github.qauxv.util.QQVersion;
import io.github.qauxv.util.SyncUtils;
import io.github.qauxv.util.dexkit.CPopOutEmoticonUtil;
import io.github.qauxv.util.dexkit.DexKit;
import io.github.qauxv.util.dexkit.DexKitTarget;
import io.github.qauxv.util.dexkit.EmoticonHandler_handleSmallEmotion;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@FunctionHookEntry
@UiItemAgentEntry
public class AddNewEmotion extends CommonSwitchFunctionHook {
    private AddNewEmotion() {
        super(SyncUtils.PROC_MAIN, new DexKitTarget[]{EmoticonHandler_handleSmallEmotion.INSTANCE});
    }

    @NonNull
    @Override
    public String getName() {
        return "添加38号小表情";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "仅在本地生效";
    }

    @NonNull
    @Override
    public String[] getUiItemLocation() {
        return FunctionEntryRouter.Locations.Auxiliary.EXPERIMENTAL_CATEGORY;
    }

    @Override
    public boolean isAvailable() {
        return requireMinQQVersion(QQVersion.QQ_8_9_28);
    }

    public static final AddNewEmotion INSTANCE = new AddNewEmotion();

    @Override
    public boolean initOnce() throws Exception {
        Method method = DexKit.requireMethodFromCache(EmoticonHandler_handleSmallEmotion.INSTANCE);
        HookUtils.hookBeforeIfEnabled(this, method, param -> {
            List<EmosmPb$SubCmd0x2RspFetchTab$TabInfo> listTabInfo = (List<EmosmPb$SubCmd0x2RspFetchTab$TabInfo>) param.args[3];
            ArrayList<String> idList = (ArrayList<String>) param.args[4];
            EmosmPb$SubCmd0x2RspFetchTab$TabInfo tabInfo = new EmosmPb$SubCmd0x2RspFetchTab$TabInfo();
            tabInfo.uint32_tab_id.set(38);
            tabInfo.fixed32_expire_time.set(0);
            tabInfo.uint32_flags.set(1);
            tabInfo.int32_wording_id.set(1);
            tabInfo.int32_tab_type.set(5);
            tabInfo.str_tab_name.set("天使恶魔小表情");
            listTabInfo.add(tabInfo);
            XposedBridge.log("Add emotion listlen:" + listTabInfo.size());
            idList.add("38");
        });
        HookUtils.hookAfterIfEnabled(this, method, param -> XposedBridge.log("Add emotion result:" + new Gson().toJson(param.args[1])));
        return true;
    }
}
