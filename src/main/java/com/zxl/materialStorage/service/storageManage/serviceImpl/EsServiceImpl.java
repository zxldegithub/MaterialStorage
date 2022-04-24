package com.zxl.materialStorage.service.storageManage.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EsMapper;
import com.zxl.materialStorage.model.enumPackage.StorageStatus;
import com.zxl.materialStorage.model.enumPackage.StorageType;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.service.storageManage.EsService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @className: ErServiceImpl
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/11
 **/
@Service
public class EsServiceImpl extends ServiceImpl<EsMapper, ErStorage> implements EsService {

    @Autowired
    private EsMapper esMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(ErStorage erStorage) throws Exception {
        if (erStorage != null) {
            //是否已经存在此编号的物资库
            ErStorage existErStorage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, erStorage.getEsNo()));
            if (existErStorage == null) {
                String esTypeName = erStorage.getEsTypeName();
                //枚举所有的物资库类型
                int esTypeCode = 0;
                boolean flag = false;
                for (StorageType value : StorageType.values()) {
                    if (value.getName().equals(esTypeName)) {
                        esTypeCode = value.getCode();
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    throw new Exception("暂无此类型的仓库");
                }
                //构建物资库对象并存入数据库
                ErStorage newErStorage = ErStorage.builder()
                        .esNo(erStorage.getEsNo())
                        .esLocation(erStorage.getEsLocation())
                        .esTypeCode(esTypeCode)
                        .esTypeName(esTypeName)
                        .esIntroduce(erStorage.getEsIntroduce())
                        .esTimeValue(SystemUtil.getTime())
                        .esTs(SystemUtil.getTime())
                        .build();
                save(newErStorage);
            } else {
                throw new Exception("已存在此仓库");
            }
        } else {
            throw new Exception("参数为空");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String esId){
        try {
            removeById(esId);
        } catch (Exception e) {
            log.error("单个删除物资库时出错",e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> esIdList){
        try {
            removeByIds(esIdList);
        } catch (Exception e) {
            log.error("批量删除物资库时出错",e);
        }
    }

    @Override
    public void updateOne(ErStorage erStorage) throws Exception {
        if (erStorage != null) {
            //根据ID获取将要更新的物资库
            ErStorage existErStorage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsId, erStorage.getEsId()));
            if (existErStorage != null) {
                //枚举所有物资库类型和状态类型
                int esTypeCode = 0;
                for (StorageType value : StorageType.values()) {
                    if (value.getName().equals(erStorage.getEsTypeName())) {
                        esTypeCode = value.getCode();
                    }
                }
                int esStatusCode = 0;
                for (StorageStatus value : StorageStatus.values()) {
                    if (value.getName().equals(erStorage.getEsStatusName())) {
                        esStatusCode = value.getCode();
                    }
                }
                //构建新对象
                existErStorage.setEsNo(erStorage.getEsNo());
                existErStorage.setEsLocation(erStorage.getEsLocation());
                existErStorage.setEsTypeCode(esTypeCode);
                existErStorage.setEsTypeName(erStorage.getEsTypeName());
                existErStorage.setEsIntroduce(erStorage.getEsIntroduce());
                existErStorage.setEsStatusCode(esStatusCode);
                existErStorage.setEsStatusName(erStorage.getEsStatusName());
                existErStorage.setEsTs(SystemUtil.getTime());
                boolean isSuccess = updateById(existErStorage);
                if (!isSuccess) {
                    throw new Exception("更新该物资库信息失败");
                }
            } else {
                throw new Exception("未查询到该条物资库记录");
            }
        } else {
            throw new Exception("参数为空");
        }

    }

    @Override
    public Page<ErStorage> selectAll(int pageIndex, int pageSize) {
        return page(new Page<>(pageIndex,pageSize));
    }
}
