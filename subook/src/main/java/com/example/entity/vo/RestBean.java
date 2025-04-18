package com.example.entity.vo;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestBean<T> {
    int code;
    T data;
    String message;
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, data, "请求成功");
    }
    public static <T> RestBean<T> success(){
        return success(null);
    }

    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, null, message);
    }

    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
