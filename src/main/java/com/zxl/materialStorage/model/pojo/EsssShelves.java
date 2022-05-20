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
 * @className: EsssShelves
 * @description: EsssShelves
 * @author: ZhangXiaolei
 * @date: 2022/4/28
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_storage_storeroom_space_shelves")
public class EsssShelves {
    @TableId(type = IdType.ASSIGN_ID)
    private String essssId;
    private String essssNo;
    private String esssNo;
    private String essNo;
    private String esNo;
    private String essssSpecifications;
    private String essssFloorFirst;
    private String essssFloorSecond;
    private String essssFloorThird;
    private String essssFloorFourth;
    private String essssFloorFifth;
    private String essssTimeValue;
    @TableField("ts")
    private String essssTs;
}
