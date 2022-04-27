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
import org.springframework.data.annotation.Id;

/**
 * @className: ErStorageStoreroom
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("er_storage_storeroom")
public class EsStoreroom {
    @TableId(type = IdType.ASSIGN_ID)
    private String essId;
    private String essNo;
    private String esNo;
    private String essLocation;
    private String essUse;
    private double essArea;
    private int essFloorNumber;
    private int essSpaceNumber;
    private String essTimeValue;
    @TableField("ts")
    private String essTs;
}
