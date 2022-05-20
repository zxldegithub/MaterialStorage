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
 * @className: MaterialPacking
 * @description: MaterialPacking
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_material_packing")
public class MaterialPacking {
    @TableId(type = IdType.ASSIGN_ID)
    private String empId;
    private String empNo;
    private Integer empNumber;
    private Double empVolume;
    private Double empWeight;
    private String empTimeValue;
    @TableField("ts")
    private String empTs;
}
