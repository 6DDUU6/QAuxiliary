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

public class NativeEncryptData {
    private String serviceCmd;
    private String imei;
    private String revision;
    private int requestSsoSeq;
    private String oldData;
    private String newData;

    public NativeEncryptData(String serviceCmd, String imei, String revision, int requestSsoSeq, String oldData, String newData) {
        this.serviceCmd = serviceCmd;
        this.imei = imei;
        this.revision = revision;
        this.requestSsoSeq = requestSsoSeq;
        this.oldData = oldData;
        this.newData = newData;
    }

    public String getServiceCmd() {
        return serviceCmd;
    }

    public void setServiceCmd(String serviceCmd) {
        this.serviceCmd = serviceCmd;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public int getRequestSsoSeq() {
        return requestSsoSeq;
    }

    public void setRequestSsoSeq(int requestSsoSeq) {
        this.requestSsoSeq = requestSsoSeq;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }
}
