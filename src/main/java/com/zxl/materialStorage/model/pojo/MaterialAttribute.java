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
 * @className: MaterialAttribute
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/5/3
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_material_attribute")
public class MaterialAttribute {
    @TableId(type = IdType.ASSIGN_ID)
    private String emaId;
    private String emaNo;
    private String emaName;
    private String emaSpecifications;
    private String emaProducer;
    private String emaSupplier;
    private String emaDateProduct;
    private String emaShelfLife;
    private String emaMaintenanceCycle;
    private String emaIsReusable;
    private String emaTimeValue;
    @TableField("ts")
    private String emaTs;
}
