package com.zxl.materialStorage.service.erStorage.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.EsMapper;
import com.zxl.materialStorage.model.dto.ErStorageDto;
import com.zxl.materialStorage.model.dto.ErStorageUpdateDto;
import com.zxl.materialStorage.model.enumPackage.StorageStatus;
import com.zxl.materialStorage.model.enumPackage.StorageType;
import com.zxl.materialStorage.model.pojo.ErStorage;
import com.zxl.materialStorage.model.vo.ErStorageVo;
import com.zxl.materialStorage.service.erStorage.EsService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public void insertNewOne(ErStorageDto erStorageDto) throws Exception {
        if (erStorageDto != null) {
            //是否已经存在此编号的物资库
            ErStorage lastErStorage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, erStorageDto.getEsNo()));
            if (lastErStorage == null) {
                String esTypeName = erStorageDto.getEsTypeName();
                //枚举所有的物资库类型
                int esTypeCode = 0;
                for (StorageType value : StorageType.values()) {
                    if (value.getName().equals(esTypeName)) {
                        esTypeCode = value.getCode();
                        break;
                    } else {
                        throw new Exception("暂无此类型的仓库");
                    }
                }
                //构建物资库对象并存入数据库
                ErStorage erStorage = ErStorage.builder()
                        .esNo(erStorageDto.getEsNo())
                        .esLocation(erStorageDto.getEsLocation())
                        .esTypeCode(esTypeCode)
                        .esTypeName(esTypeName)
                        .esIntroduce(erStorageDto.getEsIntroduce())
                        .esTimeValue(SystemUtil.getTime())
                        .esTs(SystemUtil.getTime())
                        .build();
                save(erStorage);
            } else {
                throw new Exception("已存在此仓库");
            }
        } else {
            throw new Exception("参数为空");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(ErStorageDto erStorageDto) throws Exception {
        if (erStorageDto != null) {
            //根据物资库编号进行删除
            remove(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsNo, erStorageDto.getEsNo()));
        } else {
            throw new Exception("参数为空");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<ErStorageDto> erStorageDtoList) throws Exception {
        if (!erStorageDtoList.isEmpty()) {
            //根据Dto集合封装一个物资编号集合
            List<String> esNoList = new ArrayList<>();
            for (ErStorageDto erStorageDto : erStorageDtoList) {
                esNoList.add(erStorageDto.getEsNo());
            }
            //根据物资编号集合查询满足条件的物资集合
            List<ErStorage> erStorageList = list(new QueryWrapper<ErStorage>().lambda().in(ErStorage::getEsNo, esNoList));
            if (!erStorageList.isEmpty()) {
                //根据物资集合封装一个物资ID集合
                List<Long> esIdList = new ArrayList<>();
                for (ErStorage erStorage : erStorageList) {
                    esIdList.add(erStorage.getEsId());
                }
                //根据物资ID集合进行批量删除
                boolean isSuccess = removeByIds(esIdList);
                if (!isSuccess) {
                    throw new Exception("批量删除物资库失败");
                }
            } else {
                throw new Exception("获取不到所选物资列表");
            }
        } else {
            throw new Exception("参数为空");
        }
    }

    @Override
    public void updateOne(ErStorageUpdateDto erStorageUpdateDto) throws Exception {
        if (erStorageUpdateDto != null) {
            //根据ID获取将要更新的物资库
            ErStorage erStorage = getOne(new QueryWrapper<ErStorage>().lambda().eq(ErStorage::getEsId, erStorageUpdateDto.getEsId()));
            if (erStorage != null) {
                //枚举所有物资库类型和状态类型
                int esTypeCode = 0;
                for (StorageType value : StorageType.values()) {
                    if (value.getName().equals(erStorageUpdateDto.getEsTypeName())){
                        esTypeCode = value.getCode();
                    }
                }
                int esStatusCode = 0;
                for (StorageStatus value : StorageStatus.values()) {
                    if (value.getName().equals(erStorageUpdateDto.getEsStatusName())){
                        esStatusCode = value.getCode();
                    }
                }
                //构建新对象
                erStorage.setEsNo(erStorageUpdateDto.getEsNo());
                erStorage.setEsLocation(erStorageUpdateDto.getEsLocation());
                erStorage.setEsTypeCode(esTypeCode);
                erStorage.setEsTypeName(erStorageUpdateDto.getEsTypeName());
                erStorage.setEsIntroduce(erStorageUpdateDto.getEsIntroduce());
                erStorage.setEsStatusCode(esStatusCode);
                erStorage.setEsStatusName(erStorageUpdateDto.getEsStatusName());
                erStorage.setEsTs(SystemUtil.getTime());
                boolean isSuccess = updateById(erStorage);
                if (!isSuccess){
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
    public Page<ErStorageVo> selectAll(int pageIndex, int pageSize) {
        return esMapper.selectAll(new Page<>(pageIndex, pageSize));
    }
}
