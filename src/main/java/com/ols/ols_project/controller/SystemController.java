package com.ols.ols_project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ols.ols_project.model.Result;
import com.ols.ols_project.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.HashMap;

@RestController
@RequestMapping("system")
public class SystemController {
    @Autowired
    private SystemService systemService;

    @GetMapping("/getSystemByUID")
    public String getSystemByUID(long userId){
        return null;
    }

    @GetMapping("/getAllSystemByAcceptUID")
    public String getAllSystemByAcceptUID(@RequestParam(value = "acceptUID") long acceptUID,
                                          @RequestParam(value = "page") Integer pageNum,
                                          @RequestParam(value = "limit") Integer pageSize){
        String result= JSON.toJSONStringWithDateFormat(
                new Result(
                        systemService.getAllSystemByAcceptUID(acceptUID,pageNum,pageSize)
                        ,"0"
                        ,"获取所有任务成功")
                ,"yyyy-MM-dd hh:mm:ss"
                , SerializerFeature.WriteNonStringValueAsString);
        return result;
    }
    @PostMapping("/setViewed")
    public String setViewed(long id) {
        systemService.setViewed(id);
        return "200";
    }
    @GetMapping("/getSystemById")
    public String getSystemById(long id){
        HashMap<String, Object> data = new HashMap<>();
        data.put("systemInfo", systemService.getSystemById(id));
        String result= JSON.toJSONStringWithDateFormat(
                new Result(data
                        ,"200"
                        ,"获取通知成功")
                ,"yyyy-MM-dd hh:mm:ss"
                , SerializerFeature.WriteNonStringValueAsString);
        return result;
    }
}