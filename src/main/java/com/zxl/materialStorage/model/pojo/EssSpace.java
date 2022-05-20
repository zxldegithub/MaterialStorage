package com.zxl.materialStorage.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @className: EssSpace
 * @description: EssSpace
 * @author: ZhangXiaolei
 * @date: 2022/4/27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_storage_storeroom_space")
public class EssSpace {
    @TableId(type = IdType.ASSIGN_ID)
    private String esssId;
    private String esssNo;
    private String essNo;
    private String esNo;
    private Integer esssFloorLocation;
    private String esssTimeValue;
    @TableField("ts")
    private String esssTs;
}
