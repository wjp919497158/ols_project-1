package com.ols.ols_project.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baidu.fsg.uid.service.UidGenService;
import com.ols.ols_project.common.Const.FileTypeEnum;
import com.ols.ols_project.common.Const.IsPassedEnum;
import com.ols.ols_project.common.Const.TaskStateEnum;
import com.ols.ols_project.mapper.TaskMapper;
import com.ols.ols_project.model.*;
import com.ols.ols_project.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 任务相关的service实现类
 * @author yuyy
 * @date 20-2-24 下午7:13
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UidGenService uidGenService;

    @Override
    public String getImageListByTaskId(long taskId) {
        return taskMapper.getImageListByTaskId(taskId);
    }

    @Override
    public AcceptTaskForTaskInfo getAccepteImageListByAccepteId(long acceptId) { return taskMapper.getAccepteImageListByAccepteId(acceptId);
    }

    @Override
    public int storeImageLabelInfo(long acceptTaskId, List<LabelInfo> labelInfos, String imageUrlParam) {
        AccepteEntity acceptEntity = taskMapper.getAccepteTaskInfoByAcceptId(acceptTaskId);
        AccepteImageUrl acceptImageUrl = JSON.parseObject(acceptEntity.getUrl(), new TypeReference<AccepteImageUrl>() {});
        acceptImageUrl.getTaskImage().stream().forEach(e->{
            if(e.getOriginalImage().equals(imageUrlParam)){
                e.setLabeledInfo(labelInfos);
                e.setIsLabeled(true);
                e.setIsExample(false);
                return;
            }
        });
        return taskMapper.storeImageLabelInfoByAccepteId(acceptTaskId,JSON.toJSONString(acceptImageUrl));
    }

    @Override
    public List<TaskEntityBo> getNotCheckedTask(long userId) {
        List<TaskEntity> entityList = taskMapper.getNotCheckedTask(userId);
        List<TaskEntityBo> entryListBo=new ArrayList<>();
        entityList.stream().forEach(
                taskEntity -> {
                    entryListBo.add(
                            TaskEntityBo.builder()
                                    .id(taskEntity.getId())
                                    .name(taskEntity.getName())
                                    .url(taskEntity.getUrl())
                                    .information(taskEntity.getInformation())
                                    .points(taskEntity.getPoints())
                                    .state(TaskStateEnum.CHECK.getName())
                                    .type(FileTypeEnum.getNameByCode(taskEntity.getType()))
                                    .release_time(taskEntity.getRelease_time())
                                    .release_user_id(taskEntity.getRelease_user_id())
                                    .ext1(taskEntity.getExt1())
                                    .ext2(taskEntity.getExt2())
                                    .ext3(taskEntity.getExt3())
                                    .build()
                    );
                }
        );
        return entryListBo;
    }

    @Override
    public int setNotCheckedTaskForUser(long userId, int count) {
        return taskMapper.setNotCheckedTaskForUser(userId,count);
    }

    @Override
    public TaskEntityBo getTaskInfoByTaskId(long taskId) {
        TaskEntity taskEntity = taskMapper.getTaskInfoByTaskId(taskId);
        String state=null;
        switch (taskEntity.getState()){
            case 1:state="已完成";break;
            case 2:state="已删除";break;
            case 3:state="已失效";break;
            case 4:state="审核中";break;
            case 5:state="已发布";break;
            case 6:state="未通过审核";break;
            default :state="";break;
        }
        String type=null;
        switch (taskEntity.getType()){
            case 0:type ="文档";break;
            case 1:type="图片";break;
            default :type="";break;
        }
        return TaskEntityBo.builder()
                .id(taskEntity.getId())
                .name(taskEntity.getName())
                .url(taskEntity.getUrl())
                .information(taskEntity.getInformation())
                .points(taskEntity.getPoints())
                .state(state)
                .type(type)
                .release_time(taskEntity.getRelease_time())
                .release_user_id(taskEntity.getRelease_user_id())
                .ext1(taskEntity.getExt1())
                .ext2(taskEntity.getExt2())
                .ext3(taskEntity.getExt3())
                .build();
    }

    @Override
    public int acceptTask(long userId, long taskId) {
        int result=0;
        //查询任务信息
        TaskEntityBo taskInfoByTaskId = getTaskInfoByTaskId(taskId);
        //写入任务接受表
        result+=taskMapper.insAcceptTask(AccepteEntity.builder()
                .id(uidGenService.getUid())
                .task_id(taskInfoByTaskId.getId())
                .user_id(userId)
                .accept_time(new Timestamp(System.currentTimeMillis()))
                .state(0)
                .url(taskInfoByTaskId.getUrl())
                .build());
        //获取该任务的接受人数
        int acceptNum = taskMapper.selAcceptNumOfTask(taskId);
        //重新写入该任务的接收人数
        result+=taskMapper.updAcceptNumOfTask(taskId,++acceptNum);
        return result;
    }

    @Override
    public int taskPassOrNotPassAudits(long userId, long taskId, String operation,String message) {
        int count=taskMapper.taskPassOrNotPassAudits(taskId,operation);
        int ispassed=operation.equals("yes")?1:0;
        if(count==1){
            count=taskMapper.insJudge(JudgeEntity.builder()
                    .id(uidGenService.getUid())
                    .user_id(userId)
                    .task_id(taskId)
                    .ispassed(ispassed)
                    .isfirst(1)
                    .message(message)
                    .judge_time(new Timestamp(System.currentTimeMillis()))
                    .build());
        }
        return count;
    }

    @Override
    public HashMap<String, Object> getFinishCheckTaskByUserId(long userId,String queryInfo, String searchInfo, int pageNum, int pageSize) {
        List<List<FinishCheckTask>> list = taskMapper.selFinishCheckTaskByUserId(userId,queryInfo, searchInfo, (pageNum - 1) * pageSize, pageSize);
        List<FinishCheckTaskBo> boList=new ArrayList<>();
        HashMap<String,Object> resultMap=new HashMap<>();
        list.get(0).forEach(e->{
            boList.add(FinishCheckTaskBo.builder()
                    .taskId(e.getTaskId())
                    .taskName(e.getTaskName())
                    .releaseTime(e.getReleaseTime())
                    .releaseUserName(e.getReleaseUserName())
                    .judgeId(e.getJudgeId())
                    .isPassed(IsPassedEnum.getNameByCode(e.getIsPassed()))
                    .message(e.getMessage())
                    .judgeTime(e.getJudgeTime())
                    .build());
        });
        resultMap.put("total",list.get(1).get(0));
        resultMap.put("taskList",boList);
        return resultMap;
    }
}
