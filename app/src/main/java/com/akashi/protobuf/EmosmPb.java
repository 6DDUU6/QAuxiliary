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

package com.akashi.protobuf;

import com.akashi.pb.MessageMicro;
import com.akashi.pb.PBField;
import com.akashi.pb.PBFixed32Field;
import com.akashi.pb.PBInt32Field;
import com.akashi.pb.PBStringField;
import com.akashi.pb.PBUInt32Field;

public final class EmosmPb {
    public static final class SubCmd0x2RspFetchTab {
        public static final class TabInfo extends MessageMicro<TabInfo> {
            static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 21, 24, 32, '(', '2'},
                    new String[]{"uint32_tab_id", "fixed32_expire_time", "uint32_flags", "int32_wording_id", "int32_tab_type", "str_tab_name"},
                    new Object[]{0, 0, 0, 0, 0, ""}, TabInfo.class);
            public final PBFixed32Field fixed32_expire_time = PBField.initFixed32(0);
            public final PBInt32Field int32_tab_type = PBField.initInt32(0);
            public final PBInt32Field int32_wording_id = PBField.initInt32(0);
            public final PBStringField str_tab_name = PBField.initString("");
            public final PBUInt32Field uint32_flags = PBField.initUInt32(0);
            public final PBUInt32Field uint32_tab_id = PBField.initUInt32(0);
        }
    }
}
