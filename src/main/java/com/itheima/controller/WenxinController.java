package com.itheima.controller;


    import com.alibaba.fastjson.JSON;
    import com.itheima.config.WenXinConfig;
    import com.alibaba.fastjson.JSONObject;
    import lombok.extern.slf4j.Slf4j;
    import okhttp3.*;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import javax.annotation.Resource;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/wenxin")
public class WenxinController {

    @Resource
    private WenXinConfig wenXinConfig;

    //历史对话，需要按照user,assistant
    List<Map<String,String>> messages = new ArrayList<>();

    /**
     * 非流式问答
     * @param question 用户的问题
     * @return
     * @throws IOException
     */
    @PostMapping("/ask")
    public String test1(String question) throws IOException {
        if(question == null || question.equals("")){
            return "请输入问题";
        }
        String responseJson = null;
        //先获取令牌然后才能访问api
        if (wenXinConfig.flushAccessToken() != null) {
            HashMap<String, String> user = new HashMap<>();
            user.put("role","user");
            user.put("content",question);
            messages.add(user);
            String requestJson = constructRequestJson(1,0.95,0.8,1.0,false,messages);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestJson);
            Request request = new Request.Builder()
                    .url(wenXinConfig.ERNIE_Bot_4_0_URL + "?access_token=" + wenXinConfig.flushAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
            try {
                responseJson = HTTP_CLIENT.newCall(request).execute().body().string();
                //将回复的内容转为一个JSONObject
                JSONObject responseObject = JSON.parseObject(responseJson);
                //将回复的内容添加到消息中
                HashMap<String, String> assistant = new HashMap<>();
                assistant.put("role","assistant");
                assistant.put("content",responseObject.getString("result"));
                messages.add(assistant);
            } catch (IOException e) {
                log.error("网络有问题");
                return "网络有问题，请稍后重试";
            }
        }
        return responseJson;
    }

    /**
     * 构造请求的请求参数
     * @param userId
     * @param temperature
     * @param topP
     * @param penaltyScore
     * @param messages
     * @return
     */
    public String constructRequestJson(Integer userId,
                                       Double temperature,
                                       Double topP,
                                       Double penaltyScore,
                                       boolean stream,
                                       List<Map<String, String>> messages) {
        Map<String,Object> request = new HashMap<>();
        request.put("user_id",userId.toString());
        request.put("temperature",temperature);
        request.put("top_p",topP);
        request.put("penalty_score",penaltyScore);
        request.put("stream",stream);
        request.put("messages",messages);
        System.out.println(JSON.toJSONString(request));
        return JSON.toJSONString(request);
    }
}