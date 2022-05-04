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
 * @className: MaterialEnter
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_material_enter")
public class MaterialEnter {
    @TableId(type = IdType.ASSIGN_ID)
    private String emeId;
    private String emeNo;
    private String emaName;
    private String emtNo;
    private String empNo;
    private String emaNo;
    private String emeType;
    private String esNo;
    private String essNo;
    private String esssNo;
    private String essssNo;
    private Double emePriceUnit;
    private Integer emeNumberCount;
    private Double emePriceCount;
    private String emeTimeValue;
    private boolean emeIsAccept;
    @TableField("ts")
    private String emeTs;
}
