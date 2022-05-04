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
 * @className: MaterialType
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_material_type")
public class MaterialType {
    @TableId(type = IdType.ASSIGN_ID)
    private String emtId;
    private String emtNo;
    private String emtNameFirst;
    private String emtNameSecond;
    private String emtTimeValue;
    @TableField("ts")
    private String emtTs;
}
