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

import static de.robv.android.xposed.XposedBridge.log;
import static io.github.qauxv.util.Initiator.load;

import android.content.Context;
import cc.ioctl.util.HttpUtil;
import com.google.gson.Gson;
import com.qq.taf.jce.HexUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import io.github.qauxv.util.McHookStatus;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author xx
 * @desc:
 * @date 2022/2/15 11:16
 */
public class PacketHook {

    public static final PacketHook INSTANCE = new PacketHook();
    private static boolean isInit = false;
    private static final String guid = getRandomGuid();

    PacketHook() {
    }

    public static String getRandomGuid() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 32; i++) {
            stringBuffer.append(new Random().nextInt(10));
        }
        return stringBuffer.toString();
    }

    private void hookGuidReturn(Class<?> clazz) {
        //byte[] guid = HexUtil.getRandomBytes(32);
        log("McHookTool: 随机guid: " + guid);
        XC_MethodHook generateGuid = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(HexUtil.hexStr2Bytes(guid));
            }
        };
        XC_MethodHook getGuidFromFile = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(HexUtil.hexStr2Bytes(guid));
            }
        };
        XC_MethodHook get_last_guid = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(HexUtil.hexStr2Bytes(guid));
            }
        };
        XposedBridge.hookAllMethods(clazz, "generateGuid", generateGuid);
        XposedBridge.hookAllMethods(clazz, "getGuidFromFile", getGuidFromFile);
        XposedBridge.hookAllMethods(clazz, "get_last_guid", get_last_guid);
    }

    private void hookMessageDigest(Class<?> clazz) {
        XC_MethodHook update = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                byte[] data = (byte[]) param.args[0];
                saveMd5Data("update", data);
            }
        };
        XC_MethodHook digest = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                byte[] data = (byte[]) param.getResult();
                saveMd5Data("digest", data);
            }
        };
        XposedHelpers.findAndHookMethod(clazz, "update", byte[].class, update);
        XposedHelpers.findAndHookMethod(clazz, "digest", digest);
    }

    private void hookT145(Class<?> clazz) {
        XC_MethodHook get_tlv_145 = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                byte[] guid = (byte[]) param.args[0];
                saveGUID(guid);
            }
        };
        XposedBridge.hookAllMethods(clazz, "get_tlv_145", get_tlv_145);
    }

    private void hookMd5(Class<?> clazz) {
        XC_MethodHook toMD5Byte = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Intrinsics.checkNotNullParameter(param, "param");
                byte[] castToBytes = (byte[]) param.args[0];
                Intrinsics.checkNotNullExpressionValue(castToBytes, "castToBytes(param.args[0])");
                byte[] bArr = (byte[]) param.getResult();
                if (bArr == null)  {
                    bArr = new byte[]{1};
                }
                saveMd5Data("toMD5Byte",
                        new Gson().toJson(new Md5Data(HexUtil.bytes2HexStr(castToBytes), HexUtil.bytes2HexStr(bArr))).getBytes(StandardCharsets.UTF_8));
            }
        };
        XC_MethodHook toMD5ByteString = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Intrinsics.checkNotNullParameter(param, "param");
                String str = (String) param.args[0];
                byte[] result = (byte[]) param.getResult();
                saveMd5Data("toMD5ByteString", new Gson().toJson(new Md5Data(str, HexUtil.bytes2HexStr(result))).getBytes(StandardCharsets.UTF_8));
            }
        };
        XC_MethodHook toMD5String = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Intrinsics.checkNotNullParameter(param, "param");
                String str = (String) param.args[0];
                String result = (String) param.getResult();
                saveMd5Data("toMD5String", new Gson().toJson(new Md5Data(str, result)).getBytes(StandardCharsets.UTF_8));
            }
        };
        XC_MethodHook toMD5 = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Intrinsics.checkNotNullParameter(param, "param");
                byte[] castToBytes = (byte[]) param.args[0];
                Intrinsics.checkNotNullExpressionValue(castToBytes, "castToBytes(param.args[0])");
                String result = (String) param.getResult();
                saveMd5Data("toMD5", new Gson().toJson(new Md5Data(HexUtil.bytes2HexStr(castToBytes), result)).getBytes(StandardCharsets.UTF_8));
            }
        };
        XposedHelpers.findAndHookMethod(clazz, "toMD5Byte", byte[].class, toMD5Byte);
        XposedHelpers.findAndHookMethod(clazz, "toMD5Byte", String.class, toMD5ByteString);
        XposedHelpers.findAndHookMethod(clazz, "toMD5", String.class, toMD5String);
        XposedHelpers.findAndHookMethod(clazz, "toMD5", byte[].class, toMD5);
    }

    private void hookEncodeRequest(Class<?> clazz) {
        XC_MethodHook nativeEncodeRequest = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int intValue = ((Integer) param.args[0]).intValue();
                String str = (String) param.args[1];
                String str2 = (String) param.args[2];
                String str3 = (String) param.args[3];
                String str4 = (String) param.args[4];
                String str5 = (String) param.args[5];
                String bytesToHex = HexUtil.bytes2HexStr((byte[]) param.args[6]);
                int intValue2 = ((Integer) param.args[7]).intValue();
                int intValue3 = ((Integer) param.args[8]).intValue();
                String str6 = (String) param.args[9];
                byte byteValue = ((Byte) param.args[10]).byteValue();
                byte byteValue2 = ((Byte) param.args[11]).byteValue();
                byte byteValue3 = ((Byte) param.args[12]).byteValue();
                byte[] bArr = (byte[]) param.args[13];
                byte[] bArr2 = (byte[]) param.args[14];
                byte[] bArr3 = (byte[]) param.args[15];
                byte[] bArr4 = (byte[]) param.getResult();
                if (str5.startsWith("wtlogin.")) {
                    saveNativeCryptData("nativeEncryptWtlogin",
                            new Gson().toJson(new NativeEncryptData(str5, str, str3, intValue, HexUtil.bytes2HexStr(bArr), HexUtil.bytes2HexStr(bArr4)))
                                    .getBytes(StandardCharsets.UTF_8));
                    return;
                }
                saveNativeCryptData("nativeEncrypt",
                        new Gson().toJson((new NativeEncryptData(str5, str, str3, intValue, HexUtil.bytes2HexStr(bArr), HexUtil.bytes2HexStr(bArr4))))
                                .getBytes(StandardCharsets.UTF_8));
            }
        };

        XC_MethodHook nativeOnReceData = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                byte[] bArr = (byte[]) param.args[0];
                int intValue = ((Integer) param.args[1]).intValue();
                saveNativeCryptData("nativeRece", bArr);
            }
        };

        XposedHelpers.findAndHookMethod(clazz, "nativeEncodeRequest", Integer.TYPE, String.class, String.class, String.class, String.class, String.class,
                byte[].class, Integer.TYPE, Integer.TYPE, String.class, Byte.TYPE, Byte.TYPE, Byte.TYPE, byte[].class, byte[].class, byte[].class, Boolean.TYPE,
                nativeEncodeRequest);

        XposedHelpers.findAndHookMethod(clazz, "nativeOnReceData", byte[].class, Integer.TYPE, nativeOnReceData);
    }

    private void hookCryptData(Class<?> clazz) {
        XC_MethodHook encrypt = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] != null && param.args[3] != null) {
                    if (McHookStatus.getOpenStatus() == 0) {
                        return;
                    }
                    int i = (int) param.args[1];
                    int i2 = (int) param.args[2];
                    byte[] bArr2 = (byte[]) param.args[3];
                    byte[] obj = new byte[i2];
                    System.arraycopy((byte[]) param.args[0], i, obj, 0, i2);
                    byte[] obj2 = new byte[bArr2.length];
                    System.arraycopy(bArr2, 0, obj2, 0, bArr2.length);
                    saveCryptData("encrypt", new Gson().toJson(
                                    new KeyData(HexUtil.bytes2HexStr(obj2), HexUtil.bytes2HexStr(obj), HexUtil.bytes2HexStr((byte[]) param.getResult())))
                            .getBytes(StandardCharsets.UTF_8));
                }
            }
        };
        XC_MethodHook decrypt = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] != null && param.args[3] != null) {
                    if (McHookStatus.getOpenStatus() == 0) {
                        return;
                    }
                    int i = (int) param.args[1];
                    int i2 = (int) param.args[2];
                    byte[] bArr2 = (byte[]) param.args[3];
                    byte[] obj = new byte[i2];
                    System.arraycopy((byte[]) param.args[0], i, obj, 0, i2);
                    byte[] obj2 = new byte[bArr2.length];
                    System.arraycopy(bArr2, 0, obj2, 0, bArr2.length);
                    saveCryptData("decrypt", new Gson().toJson(
                                    new KeyData(HexUtil.bytes2HexStr(obj2), HexUtil.bytes2HexStr(obj), HexUtil.bytes2HexStr((byte[]) param.getResult())))
                            .getBytes(StandardCharsets.UTF_8));
                }
            }
        };

        XposedBridge.hookAllMethods(clazz, "encrypt", encrypt);
        XposedBridge.hookAllMethods(clazz, "decrypt", decrypt);
    }

    private void hookECDHKey(Class<?> clazz) {
        XC_MethodHook setShareKey = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (McHookStatus.getOpenStatus() == 0) {
                    return;
                }
                saveECDHKey("setShareKey", (byte[]) param.args[0]);
            }
        };

        XC_MethodHook getShareKey = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (McHookStatus.getOpenStatus() == 0) {
                    return;
                }
                saveECDHKey("getShareKey", (byte[]) param.getResult());
            }
        };

        XposedBridge.hookAllMethods(clazz, "set_g_share_key", setShareKey);
        XposedBridge.hookAllMethods(clazz, "get_g_share_key", getShareKey);
    }

    private void hookSessionKey(Class<?> clazz) {
        XC_MethodHook putSiginfo = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                byte[] bArr = ((byte[][]) param.args[17])[3];
                saveSessionKey(bArr);
            }
        };
        XposedHelpers.findAndHookMethod(clazz, "put_siginfo", Long.TYPE, Long.TYPE, Long.TYPE, Long.TYPE, Long.TYPE, byte[].class, byte[].class, byte[].class,
                byte[].class, byte[].class, byte[].class, byte[].class, byte[].class, byte[].class, byte[].class, byte[].class, byte[].class, byte[][].class,
                long[].class, Integer.TYPE, putSiginfo);
    }

    private void hookFirst(Class<?> clazz) {
        XC_MethodHook init = new XC_MethodHook() {
            // 执行方法之前执行的方法
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // hook初始化函数, 强制打开调试模式
                if (param.args.length >= 2) {
                    param.args[1] = true;
                    if (!isInit) {
                        hookReceivePacket(param.thisObject.getClass());
                        isInit = true;
                    }
                }
            }
        };
        XC_MethodHook onReceData = new XC_MethodHook() {
            // 执行方法之后执行的方法
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!isInit) {
                    hookReceivePacket(param.thisObject.getClass());
                    isInit = true;
                }
            }
        };
        XC_MethodHook encodeRequest = new XC_MethodHook() {
            // 执行方法之后执行的方法
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                hookSendPacket(param);
            }
        };
        XC_MethodHook setAccountKey = new XC_MethodHook() {
            // 执行方法之前执行的方法
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (McHookStatus.getOpenStatus() == 0) {
                    return;
                }
                // hook初始化函数, 强制打开调试模式
                if (param.args.length == 10) {
                    saveAccountKey((byte[]) param.args[1], (byte[]) param.args[2], (byte[]) param.args[3],
                            (byte[]) param.args[4], (byte[]) param.args[5], (byte[]) param.args[6],
                            (byte[]) param.args[7], (byte[]) param.args[8]);
                }
            }
        };

        XposedBridge.hookAllMethods(clazz, "init", init);
        XposedBridge.hookAllMethods(clazz, "onReceData", onReceData);
        XposedBridge.hookAllMethods(clazz, "encodeRequest", encodeRequest);
        XposedBridge.hookAllMethods(clazz, "setAccountKey", setAccountKey);
    }

    private void hookNTPacket(Class<?> clazz) {
        XC_MethodHook onReceData = new XC_MethodHook() {
            // 执行方法之后执行的方法
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!isInit) {
                    hookReceivePacketNew(param);
                    isInit = true;
                }
            }
        };
        XC_MethodHook encodeRequest = new XC_MethodHook() {
            // 执行方法之后执行的方法
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                hookSendPacketNew(param);
            }
        };

        XposedBridge.hookAllMethods(clazz, "onMSFPacketState", onReceData);
        XposedBridge.hookAllMethods(clazz, "sendPacket", encodeRequest);
    }

    private void hookSendPacket(XC_MethodHook.MethodHookParam param) {
        if (McHookStatus.getOpenStatus() == 0) {
            return;
        }
        // encode结果, 字节数组
        byte[] result = (byte[]) param.getResult();
        if (param.args.length == 17) {
            Integer seq = (Integer) param.args[0];
            String command = (String) param.args[5];
            String uin = (String) param.args[9];
            byte[] buffer = (byte[]) param.args[15];
            saveRequest(seq, command, uin, buffer);
        } else if (param.args.length == 16) {
            Integer seq = (Integer) param.args[0];
            String command = (String) param.args[5];
            String uin = (String) param.args[9];
            byte[] buffer = (byte[]) param.args[14];
            saveRequest(seq, command, uin, buffer);
        } else if (param.args.length == 14) {
            Integer seq = (Integer) param.args[0];
            String command = (String) param.args[5];
            String uin = (String) param.args[9];
            byte[] buffer = (byte[]) param.args[12];
            saveRequest(seq, command, uin, buffer);
        } else {
            log("McHookTool -> send: hook到了个不知道什么东西");
        }
    }

    private void hookReceivePacket(Class<?> clazz) {
        XC_MethodHook xcMethodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (McHookStatus.getOpenStatus() == 0) {
                    return;
                }
                Object object = param.args[1];
                byte[] buffer = (byte[]) XposedHelpers.callMethod(object, "getWupBuffer");
                String command = (String) XposedHelpers.callMethod(object, "getServiceCmd");
                String uin = (String) XposedHelpers.callMethod(object, "getUin");
                Integer ssoSeq = (Integer) XposedHelpers.callMethod(object, "getRequestSsoSeq");
                // byte[] msgCookie = (byte[]) XposedHelpers.callMethod(object, "getMsgCookie");
                saveReceive(ssoSeq, command, uin, buffer);
            }
        };
        XposedBridge.hookAllMethods(clazz, "onResponse", xcMethodHook);
    }

    private void hookSendPacketNew(XC_MethodHook.MethodHookParam param) throws NoSuchFieldException, IllegalAccessException {
        if (McHookStatus.getOpenStatus() == 0) {
            return;
        }
        Object from = param.args[0];
        if (from.getClass().getName().equals("com.tencent.mobileqq.msfcore.MSFRequestAdapter")) {
            Field cmdField = from.getClass().getDeclaredField("mCmd");
            if (!cmdField.isAccessible()) cmdField.setAccessible(true);
            String command = (String) cmdField.get(from);
            Field uinField = from.getClass().getDeclaredField("mUin");
            if (!uinField.isAccessible()) uinField.setAccessible(true);
            String uin = (String) uinField.get(from);
            Field seqField = from.getClass().getDeclaredField("mSeq");
            if (!seqField.isAccessible()) seqField.setAccessible(true);
            Integer seq = (Integer) seqField.get(from);
            Field dataField = from.getClass().getDeclaredField("mData");
            if (!dataField.isAccessible()) dataField.setAccessible(true);
            byte[] buffer = (byte[]) dataField.get(from);
            saveRequest(seq, command, uin, buffer);
        }
    }

    private void hookReceivePacketNew(XC_MethodHook.MethodHookParam param) throws NoSuchFieldException, IllegalAccessException {
        if (McHookStatus.getOpenStatus() == 0) {
            return;
        }
        Object from = param.args[0];
        if (from.getClass().getName().equals("com.tencent.mobileqq.msfcore.MSFResponseAdapter")) {
            Field cmdField = from.getClass().getDeclaredField("mCmd");
            if (!cmdField.isAccessible()) cmdField.setAccessible(true);
            String command = (String) cmdField.get(from);
            Field uinField = from.getClass().getDeclaredField("mUin");
            if (!uinField.isAccessible()) uinField.setAccessible(true);
            String uin = (String) uinField.get(from);
            Field seqField = from.getClass().getDeclaredField("mSeq");
            if (!seqField.isAccessible()) seqField.setAccessible(true);
            Integer seq = (Integer) seqField.get(from);
            Field dataField = from.getClass().getDeclaredField("mRecvData");
            if (!dataField.isAccessible()) dataField.setAccessible(true);
            byte[] buffer = (byte[]) dataField.get(from);
            saveReceive(seq, command, uin, buffer);
        }
    }

    private void saveRequest(Integer seq, String command, String uin, byte[] buffer) {
        String address = McHookStatus.getServerUrl("");
        if ("".equals(address)) {
            return;
        }
        if (buffer.length == 0) {
            buffer = new byte[]{1};
        }
        String url = String.format(address + "/send?seq=%s&command=%s&uin=%s", seq, command, uin);
        HttpUtil.post(url, buffer);
    }

    private void saveReceive(Integer seq, String command, String uin, byte[] buffer) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        if (buffer.length == 0) {
            buffer = new byte[]{1};
        }
        String url = String.format(address + "/receive?seq=%s&command=%s&uin=%s", seq, command, uin);
        HttpUtil.post(url, buffer);
    }

    private void saveECDHKey(String type, byte[] buffer) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        if (buffer.length == 0) {
            buffer = new byte[]{1};
        }
        String url = String.format(address + "/ecdh?type=%s", type);
        HttpUtil.post(url, buffer);
    }

    private void saveAccountKey(byte[] a1, byte[] a2, byte[] a3, byte[] d1, byte[] d2, byte[] s2, byte[] key, byte[] cookie) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        String url;
        if (a1.length > 0) {
            url = String.format(address + "/key?type=%s", "a1");
            HttpUtil.post(url, a1);
        }

        if (a2.length > 0) {
            url = String.format(address + "/key?type=%s", "a2");
            HttpUtil.post(url, a2);
        }

        if (a3.length > 0) {
            url = String.format(address + "/key?type=%s", "a3");
            HttpUtil.post(url, a3);
        }

        if (d1.length > 0) {
            url = String.format(address + "/key?type=%s", "d1");
            HttpUtil.post(url, d1);
        }

        if (d2.length > 0) {
            url = String.format(address + "/key?type=%s", "d2");
            HttpUtil.post(url, d2);
        }

        if (s2.length > 0) {
            url = String.format(address + "/key?type=%s", "s2");
            HttpUtil.post(url, s2);
        }

        if (key.length > 0) {
            url = String.format(address + "/key?type=%s", "key");
            HttpUtil.post(url, key);
        }

        if (cookie.length > 0) {
            url = String.format(address + "/key?type=%s", "cookie");
            HttpUtil.post(url, cookie);
        }

    }

    private void saveSessionKey(byte[] sessionKey) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        String url;
        url = String.format(address + "/key?type=%s", "sessionKey");
        HttpUtil.post(url, sessionKey);
    }

    private void saveGUID(byte[] data) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        String url;
        url = String.format(address + "/key?type=%s", "guid");
        HttpUtil.post(url, data);
    }

    private void saveCryptData(String type, byte[] buffer) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        if (buffer.length == 0) {
            buffer = new byte[]{1};
        }
        String url = String.format(address + "/crypt?type=%s", type);
        HttpUtil.post(url, buffer);
    }

    private void saveNativeCryptData(String type, byte[] buffer) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        if (buffer.length == 0) {
            buffer = new byte[]{1};
        }
        String url = String.format(address + "/nativeCrypt?type=%s", type);
        HttpUtil.post(url, buffer);
    }

    private void saveMd5Data(String type, byte[] buffer) {
        String address = McHookStatus.getServerUrl("");
        if (address == null || "".equals(address)) {
            return;
        }
        if (buffer.length == 0) {
            buffer = new byte[]{1};
        }
        String url = String.format(address + "/md5?type=%s", type);
        HttpUtil.post(url, buffer);
    }

    public void checkAndInit(Context ctx) {
        if (McHookStatus.getOpenStatus() == 1) {
            initOnce();
        }
        if (McHookStatus.getCryptStatus() == 1) {
            initCrypt();
        }
        if (McHookStatus.getMd5Status() == 1) {
            initMd5();
        }
        if (McHookStatus.getGuidStatus() == 1) {
            initRandomGuid();
        }
        saveGuid(ctx);
    }

    public boolean initOnce() {
        try {
            log("开始初始化packetHook");
            Class clz = load("com.tencent.qphone.base.util.CodecWarpper");
            if (clz == null) {
                log("McHookTool: CodecWarpper isnull");
            } else {
                hookFirst(clz);
            }
            Class clz2 = load("oicq.wlogin_sdk.tools.EcdhCrypt");
            if (clz2 == null) {
                log("McHookTool: EcdhCrypt isnull");
            } else {
                hookECDHKey(clz2);
            }
            Class clz3 = load("oicq.wlogin_sdk.tools.cryptor");
            if (clz3 == null) {
                log("McHookTool: cryptor isnull");
            } else {
                hookCryptData(clz3);
            }
            Class clz4 = load("oicq.wlogin_sdk.request.WloginAllSigInfo");
            if (clz4 == null) {
                log("McHookTool: WloginAllSigInfo isnull");
            } else {
                hookSessionKey(clz4);
            }
            Class clz5 = load("oicq.wlogin_sdk.tools.MD5");
            if (clz5 == null) {
                log("McHookTool: MD5 isnull");
            } else {
                hookMd5(clz5);
            }
            Class clz6 = load("oicq.wlogin_sdk.tlv_type.tlv_t145");
            if (clz6 == null) {
                log("McHookTool: tlv_t145 isnull");
            } else {
                hookT145(clz6);
            }
//            Class clz7 = load("oicq.wlogin_sdk.tools.util");
//            if (clz7 == null) {
//                log("McHookTool: util isnull");
//            }
//            hookGuid(clz7);
            Class clz8 = load("com.tencent.mobileqq.msfcore.MSFKernel");
            if (clz8 == null) {
                log("McHookTool: MSFKernel isnull");
            } else {
                hookNTPacket(clz8);
            }
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public boolean initCrypt() {
        try {
            log("开始初始化cryptor");
            Class clz = load("com.tencent.qphone.base.util.CodecWarpper");
            if (clz == null) {
                log("McHookTool: CodecWarpper isnull");
            }
            hookEncodeRequest(clz);
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public boolean initMd5() {
        try {
            log("开始初始化MessageDigest");
            Class clz = load("java.security.MessageDigest");
            if (clz == null) {
                log("McHookTool: MessageDigest isnull");
            }
            hookMessageDigest(clz);
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public boolean initRandomGuid() {
        try {
            log("开始初始化RandomGuid");
            Class clz = load("oicq.wlogin_sdk.tools.util");
            if (clz == null) {
                log("McHookTool: tools.util isnull");
            }
            hookGuidReturn(clz);
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public boolean saveGuid(Context ctx) {
        try {
            log("开始getGuid");
            Class clz = load("oicq.wlogin_sdk.tools.util");
            if (clz == null) {
                log("McHookTool: tools.util isnull");
            }
            byte[] guid = (byte[]) XposedHelpers.callStaticMethod(clz, "getGuidFromFile", ctx);
            McHookStatus.setGuidValue(HexUtil.bytes2HexStr(guid));
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public boolean disableNewMSF(Context ctx) {
        try {
            log("开始禁用新版msf");
            Class clz = load("com.tencent.mobileqq.msf.core.f0.b");
            if (clz == null) {
                log("McHookTool: tools.util isnull");
            }
            XC_MethodHook disableMSF = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (McHookStatus.getOpenStatus() == 0) {
                        return;
                    }
                    if (param.args.length == 2) {
                        param.args[1] = false
                    }
                }
            };
            XposedBridge.hookAllMethods(clz, "a", disableMSF);
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

}
