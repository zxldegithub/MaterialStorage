package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EssssMapper;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.model.pojo.EsssShelves;
import com.zxl.materialStorage.service.storageManage.EsssService;
import com.zxl.materialStorage.service.storageManage.EssssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @className: EssssServiceImpl
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/28
 **/

@Service
public class EssssServiceImpl extends ServiceImpl<EssssMapper, EsssShelves> implements EssssService {
    @Autowired
    private EsssService esssService;

    @Override
    public void insertNewOne(EsssShelves esssShelves) throws Exception{
        //判断是否已经存在该编号的货架
        EsssShelves existShelves = getOne(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssssNo, esssShelves.getEssssNo()));
        if (ObjectUtil.isNotNull(existShelves)){
            throw new Exception("已存在该编号的货架");
        }
        //获取各上级的编号，货区编号已经有了，还需要仓库编号和物资库编号
        EssSpace space = esssService.getOne(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsssNo, esssShelves.getEsssNo()));
        //补全货架的属性
        esssShelves.setEssNo(space.getEssNo()).setEsNo(space.getEsNo())
                .setEssssTimeValue(SystemUtil.getTime()).setEssssTs(SystemUtil.getTime());
        save(esssShelves);
    }

    @Override
    public void deleteOne(String essssId) {
        removeById(essssId);
    }

    @Override
    public void deleteMany(List<String> essssIdList) {
        removeBatchByIds(essssIdList);
    }

    @Override
    public void updateOne(EsssShelves esssShelves) {
        //比较上级货区编号是否发生变化，若发生变化，则货架相关编号信息也要发生变化
        EsssShelves byId = getById(esssShelves.getEssssId());
        if (!byId.getEsssNo().equals(esssShelves.getEsssNo())){
            EssSpace space = esssService.getOne(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsssNo, esssShelves.getEsssNo()));
            esssShelves.setEssNo(space.getEssNo()).setEsNo(space.getEsNo());
        }
        esssShelves.setEssssTs(SystemUtil.getTime());
        updateById(esssShelves);
    }

    @Override
    public Page<EsssShelves> selectByPage(Integer pageIndex, Integer pageSize) {
        return page(new Page<>(pageIndex,pageSize));
    }
}
