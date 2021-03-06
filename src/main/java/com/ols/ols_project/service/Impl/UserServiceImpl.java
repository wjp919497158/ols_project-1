package com.ols.ols_project.service.Impl;

import com.ols.ols_project.common.Const.*;
import com.ols.ols_project.mapper.UserMapper;
import com.ols.ols_project.model.*;
import com.ols.ols_project.model.entity.TaskEntity;
import com.ols.ols_project.model.entity.UserEntity;
import com.ols.ols_project.model.entity.UserOperationLogEntity;
import com.ols.ols_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户相关的Service实现类
 * @author yuyy
 * @date 20-2-19 下午2:20
 */

/**
 *修改邮箱
 * @author sf
 * @date 20-3-23
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserEntity getUserInfoById(long id) {
        return userMapper.getUserInfoById(id);
    }

    @Override
    public String getPassWodById(long id) {
        return userMapper.getPassWodById(id);
    }

    @Override
    public int changePassWordById(long id, String passWord) {
        return userMapper.changePassWordById(id,passWord);
    }

    @Override
    public HashMap<String,Object> getAcceptTaskByUserId(long userId, String query, Integer pageNum, Integer pageSize, String queryInfo, String searchInfo,String taskType) {
        List<List<AcceptTask>> list = userMapper.getAcceptTaskByUserId(userId, query, (pageNum - 1) * pageSize, pageSize,queryInfo, searchInfo,taskType);
        List<AcceptTaskBo> list1=new ArrayList<>();
        HashMap<String,Object> data=new HashMap<>();
        list.get(0).forEach(
                e->{
                    list1.add(AcceptTaskBo.builder()   //Lombok
                            .taskId(e.getTaskId())
                            .acceptId(e.getAcceptId())
                            .taskName(e.getTaskName())
                            .points(e.getPoints())
                            .taskState(TaskStateEnum.getNameByCode(e.getTaskState()))  //枚举
                            .type(FileTypeEnum.getNameByCode(e.getType()))
                            .releaseTime(e.getReleaseTime())
                            .releaseName(e.getReleaseName())
                            .acceptNum(e.getAcceptNum())
                            .acceptTime(e.getAcceptTime())
                            .finishTime(e.getFinishTime())
                            .acceptState(AcceptStateEnum.getNameByCode(e.getAcceptState()))
                            .build());
                }
        );
        data.put("taskList",list1);
        data.put("total",list.get(1).get(0));
        return data;
    }

    @Override
    public HashMap<String, Object> getReleaseTaskByUserId(long userId, String query, Integer pageNum, Integer pageSize, String queryInfo, String searchInfo,String taskType) {
        List<List<TaskEntity>> list = userMapper.getReleaseTaskByUserId(userId, query, (pageNum - 1) * pageSize, pageSize,queryInfo, searchInfo,taskType);
        List<TaskEntityBo> list1=new ArrayList<>();
        HashMap<String,Object> data=new HashMap<>();
        list.get(0).forEach(
                e->{
                    list1.add(TaskEntityBo.builder()
                            .id(e.getId())
                            .name(e.getName())
                            .information(e.getInformation())
                            .points(e.getPoints())
                            .state(TaskStateEnum.getNameByCode(e.getState()))
                            .type(FileTypeEnum.getNameByCode(e.getType()))
                            .release_time(e.getRelease_time())
                            .finish_time(e.getFinish_time())
                            .accept_num(e.getAccept_num())
                            .adopt_accept_id(e.getAdopt_accept_id())
                            .ext1(e.getExt1())
                            .ext2(e.getExt2())
                            .build());
                }
        );
        data.put("taskList",list1);
        data.put("total",list.get(1).get(0));
        return data;
    }

    @Override
    public HashMap<String, Object> getReviewerSignUp(String queryInfo,String searchInfo ,Integer pageNum, Integer pageSize) {
        List<List<UserSignUp>> list = userMapper.getReviewerSignUp( queryInfo, searchInfo ,(pageNum - 1) * pageSize, pageSize);
        HashMap<String,Object> result=new HashMap<>();
        List<Object> listBo=new ArrayList<>();
        list.get(0).stream().forEach(userEntity -> {
            listBo.add(UserSignUp.builder()
                    .id(userEntity.getId())
                    .name(userEntity.getName())
                    .birthday(userEntity.getBirthday())
                    .sex(userEntity.getSex())
                    .email(userEntity.getEmail())
                    .role(RoleEnum.REVIEWER.getName())
                    .ext1(ReviewerSignUpEnum.getNameByCode(Integer.parseInt(userEntity.getExt1())))
                    .signUpTime(userEntity.getSignUpTime())
                    .build());
        });
        result.put("total",list.get(1).get(0));
        result.put("userList",listBo);
        return result;
    }

    @Override
    public int yesAndNoReviewerSignUp(long userId, String operation) {
        return userMapper.yesAndNoReviewerSignUp(userId,operation);
    }


    @Override
    public int changeEmailById(long userId, String email) {
        return userMapper.changeEmailById(userId,email);
    }

    @Override
    public Long login(String userName,String passWord){
        return userMapper.login(userName,passWord);
    }

    @Override
    public Long checkUserName(String userName){
        return userMapper.checkUserName(userName);
    }

    @Override
    public int userLoginTime(UserOperationLogEntity userLog){return userMapper.userLoginTime(userLog);}

    @Override
    public int userRegister(UserEntity user){return userMapper.userRegister(user);}

    @Override
    public int userRegisterTime(UserOperationLogEntity userLog){return userMapper.userRegisterTime(userLog);}

    @Override
    public String getEmailByName(String userName){return userMapper.getEmailByName(userName);}

    @Override
    public int changePasswordByName(String userName,String password){return userMapper.changePasswordByName(userName,password);}

    @Override
    public HashMap<String, Object> getUserSignUp(String queryInfo,String searchInfo ,Integer pageNum, Integer pageSize) {
        List<List<UserSignUp>> list = userMapper.getUserSignUp( queryInfo, searchInfo ,(pageNum - 1) * pageSize, pageSize);
        HashMap<String,Object> result=new HashMap<>();
        List<Object> listBo=new ArrayList<>();
        list.get(0).stream().forEach(userEntity -> {
            listBo.add(UserSignUp.builder()
                    .id(userEntity.getId())
                    .name(userEntity.getName())
                    .birthday(userEntity.getBirthday())
                    .sex(userEntity.getSex())
                    .points(userEntity.getPoints())
                    .email(userEntity.getEmail())
                    .role(RoleEnum.getNameByCode(Integer.parseInt(userEntity.getRole())))
                    .signUpTime(userEntity.getSignUpTime())
                    .build());
        });
        result.put("total",list.get(1).get(0));
        result.put("userList",listBo);
        return result;
    }

    @Override
    public HashMap<String, Object> getUserOperationLog(String searchInfo,Integer pageNum, Integer pageSize,String user_id) {
        List<List<UserOperationLog>> list = userMapper.getUserOperationLog(searchInfo,(pageNum - 1) * pageSize, pageSize,user_id);
        HashMap<String,Object> result=new HashMap<>();
        List<Object> listBo=new ArrayList<>();
        list.get(0).stream().forEach(userOperationLogEntity -> {
            listBo.add(UserOperationLog.builder()
                    .id(userOperationLogEntity.getId())
                    .user_id(userOperationLogEntity.getUser_id())
                    .type(LogTypeEnum.getNameByCode(Integer.parseInt(userOperationLogEntity.getType())))
                    .operation(userOperationLogEntity.getOperation())
                    .time(userOperationLogEntity.getTime())
                    .build());
        });
        result.put("total",list.get(1).get(0));
        result.put("logList",listBo);
        return result;
    }

    @Override
    public int deleteUser(String userId){return userMapper.deleteUser(userId);}

    @Override
    public List<UserEntity> getPointsRank() {
        List<UserEntity> list = userMapper.getPointsRank();
        return list;
    }

    @Override
    public int userOperation(UserOperationLogEntity userLog){return userMapper.userOperation(userLog);}

    @Override
    public int[] getSex(int role) {
        int[] resultArr=new int[3];
        //获取性别为男的数量
        List<SexAndCount> men = userMapper.getSex(role,"男");
        //获取性别为女的数量
        List<SexAndCount> women = userMapper.getSex(role,"女");
        resultArr[0]=Integer.parseInt(men.get(0).getCount());
        resultArr[1]=Integer.parseInt(women.get(0).getCount());;
        resultArr[2]=resultArr[0]+resultArr[1];
        return resultArr;
    }

    @Override
    public List<DayAndCount> getRegisterday(int year){
        //获取普通用户的注册数
        List<DayAndCount> list = userMapper.getRegisterday(year);

        return list;
    }

    @Override
    public int[][] getRegister(int year){
        int[][] resultArr=new int[3][];
        //获取普通用户的注册数
        List<MonthAndCount> list0 = userMapper.getRegister(year,0);
        //获取管理员的注册数
        List<MonthAndCount> list1 = userMapper.getRegister(year,1);
        //获取审核者的注册数
        List<MonthAndCount> list2 = userMapper.getRegister(year,2);
        //普通用户
        int[] user=new int[12];
        //管理员
        int[] admin=new int[12];
        //审核者
        int[] reviewer=new int[12];
        int j=0,k=0,n=0;
        for (int i=0;i<12;i++){
            user[i]=0;
            admin[i]=0;
            reviewer[i]=0;
            if(j<list0.size()&&Integer.parseInt(list0.get(j).getMonth())==(i+1)){
                user[i]=Integer.parseInt(list0.get(j++).getCount());
            }
            if(k<list1.size()&&Integer.parseInt(list1.get(k).getMonth())==(i+1)){
                admin[i]=Integer.parseInt(list1.get(k++).getCount());
            }
            if(n<list2.size()&&Integer.parseInt(list2.get(n).getMonth())==(i+1)){
                reviewer[i]=Integer.parseInt(list2.get(n++).getCount());
            }

        }
        resultArr[0]=user;
        resultArr[1]=admin;
        resultArr[2]=reviewer;
        return resultArr;
    }

}
