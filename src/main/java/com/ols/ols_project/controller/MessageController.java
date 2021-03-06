package com.ols.ols_project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baidu.fsg.uid.service.UidGenService;
import com.ols.ols_project.model.Result;
import com.ols.ols_project.model.entity.UserOperationLogEntity;
import com.ols.ols_project.service.MessageService;
import com.ols.ols_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

//关于举报信息的controller
@RestController
@RequestMapping("message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UidGenService uidGenService;
    //发布举报信息
    @GetMapping("/createMessage")
    public String createMessage(@RequestParam("userId") String userId,
                                @RequestParam("taskId") String taskId,
                                @RequestParam("Message") String Message,
                                @RequestParam("type") int type
                                ){
        messageService.createMessage(Long.parseLong(userId),Long.parseLong(taskId),Message,type);
        String resultStr = JSON.toJSONString(new Result("200","创建举报信息成功"));
        //更新操作日志
        UserOperationLogEntity userLog=new UserOperationLogEntity();
        userLog.setId(uidGenService.getUid());
        userLog.setUser_id(Long.parseLong(userId));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        userLog.setTime(Timestamp.valueOf(df.format(date)));
        userLog.setOperation("举报任务"+taskId+"("+Message+")");
        userService.userOperation(userLog);
        return resultStr;
    }
//查询所有举报信息
    @GetMapping("/getAllMessage")
    public String getAllMessage(
                                @RequestParam(value = "page") Integer pageNum,
                                @RequestParam(value = "limit") Integer pageSize,
                                @RequestParam(value = "queryInfo") String queryInfo,
                                @RequestParam(value = "searchInfo") String searchInfo
                                ){
        String result= JSON.toJSONStringWithDateFormat(
                new Result(
                        messageService.getAllMessage( queryInfo, searchInfo ,pageNum, pageSize)
                        ,"0"
                        ,"获取所有举报信息成功")
                ,"yyyy-MM-dd hh:mm:ss"
                , SerializerFeature.WriteNonStringValueAsString);
        return result;
    }
    //回复举报信息
    @GetMapping("/replyMessage")
    public String replyMessage(@RequestParam("Response") String Response,@RequestParam("Id") long Id)
    {
        String resultStr=null;
        if(1==messageService.replyMessage(Id,Response)){
            resultStr = JSON.toJSONString(new Result("200","回复举报息成功"));;
        }else{
            resultStr = JSON.toJSONString(new Result("201","回复举报息失败"));;
        }
        return resultStr;
    }
    //举报信息可视化
    @GetMapping("/getmessage")
    public String getmessage(
            @RequestParam("year") String year
    ){
        System.out.println("111111111");
        HashMap<String, Object> data = new HashMap<>();
        data.put("complainList",
                messageService.getmessage(Integer.parseInt(year)));
        String result= JSON.toJSONStringWithDateFormat(
                new Result(data,"200","获取举报信息成功"),
                "yyyy-MM-dd");
        return result;
    }
    //用户查询举报信息的回复信息
    @GetMapping(value = "/getcomplainById")
    public String getcomplainById(
            @RequestParam(value = "userId") String userId,
            @RequestParam("page") int pageNum,
            @RequestParam("limit") int pageSize
    ) {
        // layui默认数据表格的status为0才显示数据
        String result = JSON.toJSONStringWithDateFormat(
                new Result(
                        messageService.getcomplainById(Long.parseLong(userId),pageNum, pageSize)
                        , "0"
                        , "获取奖励惩罚信息成功")
                , "yyyy-MM-dd hh:mm:ss"
                , SerializerFeature.WriteNonStringValueAsString
        );
        return result;
    }
}
