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

package cc.ioctl.hook.entertainment

import cc.ioctl.util.hookBeforeIfEnabled
import io.github.qauxv.base.annotation.FunctionHookEntry
import io.github.qauxv.base.annotation.UiItemAgentEntry
import io.github.qauxv.dsl.FunctionEntryRouter
import io.github.qauxv.hook.CommonSwitchFunctionHook
import io.github.qauxv.util.QQVersion
import io.github.qauxv.util.dexkit.DexKit
import io.github.qauxv.util.dexkit.FriendProfileImageActivity_q3
import io.github.qauxv.util.dexkit.ZPlanApiImpl_isZPlanAvatarSettingEnable
import io.github.qauxv.util.requireMinQQVersion

@FunctionHookEntry
@UiItemAgentEntry
object EnableDynamicAvatar : CommonSwitchFunctionHook(arrayOf(ZPlanApiImpl_isZPlanAvatarSettingEnable, FriendProfileImageActivity_q3)) {

    override val name = "允许设置动态头像"

    override val description = "查看有动态头像的QQ资料卡时会显示"

    override val uiItemLocation: Array<String> = FunctionEntryRouter.Locations.Entertainment.ENTERTAIN_CATEGORY

    override val isAvailable: Boolean get() = requireMinQQVersion(QQVersion.QQ_8_9_28)

    override fun initOnce(): Boolean {
        val isZPlanAvatarSettingEnable = DexKit.requireMethodFromCache(ZPlanApiImpl_isZPlanAvatarSettingEnable)
        hookBeforeIfEnabled(isZPlanAvatarSettingEnable) {
            it.result = true
        }
        val q3 = DexKit.requireMethodFromCache(FriendProfileImageActivity_q3)
        hookBeforeIfEnabled(q3) {
            it.result = true
        }
        return true
    }

}
