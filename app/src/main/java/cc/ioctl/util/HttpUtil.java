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

package cc.ioctl.util;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * @author weiguan
 * @desc:
 * @date 2022/2/16 14:15
 */
public class HttpUtil {
    public static void post(String url, byte[] data) {
        AsyncHttpClient client = new AsyncHttpClient();
        BoundRequestBuilder builder = client.preparePost(url);
        builder.setBody(data);
        ListenableFuture<Response> future = client.executeRequest(builder.build());
        future.addListener(() -> {
            try {
                Response response = future.get();
                // 处理响应结果
            } catch (Exception e) {
                // 处理异常情况
            }
        }, Executors.newSingleThreadExecutor());
    }

    public static void get(String url, byte[] data) {
        AsyncHttpClient client = new AsyncHttpClient();
        BoundRequestBuilder builder = client.prepareGet(url);
        //builder.setBody(data);
        ListenableFuture<Response> future = client.executeRequest(builder.build());
        future.addListener(() -> {
            try {
                Response response = future.get();
                // 处理响应结果
            } catch (Exception e) {
                // 处理异常情况
            }
        }, Executors.newSingleThreadExecutor());
    }
}
