package com.zxl.materialStorage.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zxl.materialStorage.model.enumPackage.StorageStatus;
import com.zxl.materialStorage.model.enumPackage.StorageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @className: EsStorage
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_storage")
public class ErStorage {
    @TableId(type = IdType.ASSIGN_ID)
    private Long esId;
    private String esNo;
    private String esLocation;
    private int esTypeCode;
    private String esTypeName;
    private int esStoreroomNumber;
    private int esOutSpaceNumber;
    private String esIntroduce;
    private int esStatusCode;
    private String esStatusName;
    private String esTimeValue;
    @TableField("ts")
    private String esTs;

}
