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

package cc.ioctl.hook.experimental

import cc.ioctl.util.hookBeforeIfEnabled
import io.github.qauxv.base.annotation.FunctionHookEntry
import io.github.qauxv.base.annotation.UiItemAgentEntry
import io.github.qauxv.dsl.FunctionEntryRouter
import io.github.qauxv.hook.CommonSwitchFunctionHook
import io.github.qauxv.util.QQVersion
import io.github.qauxv.util.dexkit.DexKit
import io.github.qauxv.util.dexkit.GrayCheckHandler_Check
import io.github.qauxv.util.dexkit.PaiYiPaiHandler_canSendReq
import io.github.qauxv.util.requireMinQQVersion

@FunctionHookEntry
@UiItemAgentEntry
object DisableGrayUinCheck : CommonSwitchFunctionHook(arrayOf(GrayCheckHandler_Check)) {

    override val name = "屏蔽内测提示"

    override val description = "就是把返回的类置null了"

    override val uiItemLocation: Array<String> = FunctionEntryRouter.Locations.Auxiliary.EXPERIMENTAL_CATEGORY

    override fun initOnce(): Boolean {
        val grayCheck = DexKit.requireMethodFromCache(GrayCheckHandler_Check)
        hookBeforeIfEnabled(grayCheck) {
            it.args[0] = null
        }
        return true
    }

}
