package com.ols.ols_project.model;

import lombok.*;

import java.sql.Timestamp;

/**
 * 已接受的任务
 * @author yuyy
 * @date 20-2-20 下午2:20
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptTaskForTaskInfoVO {
    /**
     * 任务编号
     */
    private Long ols_task_id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 原始文件路径
     */
    private String ols_task_url;

    /**
     * 详细信息
     */
    private String information;

    /**
     * 任务分值
     */
    private Integer points;

    /**
     * 状态
     */
    private String ols_task_state;

    /**
     * 文件类型
     */
    private String  type;

    /**
     * 完成时间
     */
    private Timestamp ols_task_finish_time;

    /**
     * 发布时间
     */
    private Timestamp release_time;

    /**
     * 发布者名称
     */
    private String release_user_name;

    /**
     * 接受者数量
     */
    private Integer accept_num;

    /**
     * 采纳的接受任务编号
     */
    private Long adopt_accept_id;

    /**
     * 接受任务编号
     */
    private Long ols_accept_id;

    /**
     * 接受者编号
     */
    private Long user_id;

    /**
     * 接受时间
     */
    private Timestamp accept_time;

    /**
     * 提交时间
     */
    private Timestamp ols_accept_finish_time;

    /**
     * 接受状态
     */
    private String ols_accept_state;

    /**
     * 标注文件路径
     */
    private String ols_accept_url;
}
