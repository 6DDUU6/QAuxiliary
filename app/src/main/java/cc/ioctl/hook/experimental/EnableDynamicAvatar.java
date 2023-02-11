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
import io.github.qauxv.base.annotation.FunctionHookEntry;
import io.github.qauxv.base.annotation.UiItemAgentEntry;
import io.github.qauxv.dsl.FunctionEntryRouter;
import io.github.qauxv.hook.CommonSwitchFunctionHook;
import io.github.qauxv.util.Initiator;
import io.github.qauxv.util.QQVersion;
import io.github.qauxv.util.SyncUtils;
import io.github.qauxv.util.dexkit.DexKitTarget;
import java.lang.reflect.Method;
import java.util.Objects;

@FunctionHookEntry
@UiItemAgentEntry
public class EnableDynamicAvatar extends CommonSwitchFunctionHook {

    @NonNull
    @Override
    public String getName() {
        return "允许设置动态头像";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "查看有动态头像的QQ资料卡时会显示,仅支持8.9.28";
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

    public static final EnableDynamicAvatar INSTANCE = new EnableDynamicAvatar();

    private EnableDynamicAvatar() {
        super(SyncUtils.PROC_MAIN, new DexKitTarget[]{});
    }

    @Override
    public boolean initOnce() throws Exception {
        {
            Class<?> ZPlanApiImpl = Initiator.load("com/tencent/mobileqq/zplan/api/impl/ZPlanApiImpl");
            Objects.requireNonNull(ZPlanApiImpl, "ZPlanApiImpl is null");
            if (ZPlanApiImpl != null) {
                Method method = ZPlanApiImpl.getDeclaredMethod("isZPlanAvatarSettingEnable");
                Objects.requireNonNull(method, "isZPlanAvatarSettingEnable method is null");
                HookUtils.hookBeforeIfEnabled(this, method, 47, param -> param.setResult(true));
            }
            Class<?> WinkEditorResourceAPIImpl = Initiator.load("com/tencent/mobileqq/wink/api/impl/WinkEditorResourceAPIImpl");
            Objects.requireNonNull(WinkEditorResourceAPIImpl, "WinkEditorResourceAPIImpl is null");
            if (WinkEditorResourceAPIImpl != null) {
                Method methodq = WinkEditorResourceAPIImpl.getDeclaredMethod("queryAB");
                Objects.requireNonNull(methodq, "queryAB method is null");
                HookUtils.hookBeforeIfEnabled(this, methodq, 47, param -> param.setResult(true));
            }
        }
        return true;
    }
}
