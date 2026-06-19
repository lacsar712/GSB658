package com.community.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("question_bank")
public class QuestionBank {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private String optionsJson;
    private String correctAnswer;
    private Integer rewardPoints;
    private Date createTime;
}
