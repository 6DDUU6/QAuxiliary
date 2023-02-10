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

package io.github.qauxv.util;


import static de.robv.android.xposed.XposedBridge.log;

import java.io.IOException;

import io.github.qauxv.config.ConfigManager;

public class McHookStatus {
    public static final String mc_tool_open = "mc_tool_open";
    public static final String mc_tool_open_crypt = "mc_tool_open_crypt";
    public static final String mc_tool_open_md5 = "mc_tool_open_md5";
    public static final String mc_tool_open_guid = "mc_tool_open_guid";
    public static final String mc_tool_url = "mc_tool_url";
    public static final String mc_guid_value = "mc_guid_value";

    public static int getOpenStatus() {
        return ConfigManager.getDefaultConfig().getIntOrDefault(mc_tool_open, 0);
    }

    public static void setOpenStatus(int status) {
        ConfigManager.getDefaultConfig().putInt(mc_tool_open, status);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (Exception e) {
            log(e);
            Toasts.error(HostInfo.getHostInfo().getApplication(), e.toString());
        }
    }

    public static int getCryptStatus() {
        return ConfigManager.getDefaultConfig().getIntOrDefault(mc_tool_open_crypt, 0);
    }

    public static void setCryptStatus(int status) {
        ConfigManager.getDefaultConfig().putInt(mc_tool_open_crypt, status);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (Exception e) {
            log(e);
            Toasts.error(HostInfo.getHostInfo().getApplication(), e.toString());
        }
    }

    public static int getMd5Status() {
        return ConfigManager.getDefaultConfig().getIntOrDefault(mc_tool_open_md5, 0);
    }

    public static void setMd5Status(int status) {
        ConfigManager.getDefaultConfig().putInt(mc_tool_open_md5, status);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (Exception e) {
            log(e);
            Toasts.error(HostInfo.getHostInfo().getApplication(), e.toString());
        }
    }

    public static int getGuidStatus() {
        return ConfigManager.getDefaultConfig().getIntOrDefault(mc_tool_open_guid, 0);
    }

    public static void setGuidStatus(int status) {
        ConfigManager.getDefaultConfig().putInt(mc_tool_open_guid, status);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (Exception e) {
            log(e);
            Toasts.error(HostInfo.getHostInfo().getApplication(), e.toString());
        }
    }

    public static String getServerUrl(String defaultValue) {
        return ConfigManager.getDefaultConfig().getStringOrDefault(mc_tool_url, defaultValue);
    }

    public static void setServerUrl(String url) {
        ConfigManager.getDefaultConfig().putString(mc_tool_url, url);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (Exception e) {
            log(e);
            Toasts.error(HostInfo.getHostInfo().getApplication(), e.toString());
        }
    }
    public static String getGuidValue(String defaultValue) {
        return ConfigManager.getDefaultConfig().getStringOrDefault(mc_guid_value, defaultValue);
    }

    public static void setGuidValue(String guid) {
        ConfigManager.getDefaultConfig().putString(mc_guid_value, guid);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (Exception e) {
            log(e);
            Toasts.error(HostInfo.getHostInfo().getApplication(), e.toString());
        }
    }
}
