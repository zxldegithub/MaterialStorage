package com.zxl.materialStorage.service.storageManage.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.storageManage.EsssMapper;
import com.zxl.materialStorage.model.pojo.EsStoreroom;
import com.zxl.materialStorage.model.pojo.EssSpace;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.service.storageManage.EsssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @className: EsssServiceImpl
 * @description: TODO
 * @author: ZhangXiaolei
 * @date: 2022/4/27
 **/
@Service
public class EsssServiceImpl extends ServiceImpl<EsssMapper, EssSpace> implements EsssService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private EssService essService;

    @Override
    public void insertNewOne(EssSpace essSpace) throws Exception{
        //从Redis中寻找是否已存在该库区记录
        Set<EssSpace> essSpaces = selectSetFromRedis();
        EssSpace existEssSpace = null;
        for (EssSpace space : essSpaces) {
            if (essSpace.getEssNo().equals(space.getEssNo())) {
                existEssSpace = space;
                break;
            }
        }
        if (ObjectUtil.isNull(existEssSpace)){
            //从Redis中获取上级仓库信息，并更新
            Set<EsStoreroom> esStoreroomSet = essService.selectSetFromRedis();
            String esNo = null;
            EsStoreroom existEsStoreroom = null;
            for (EsStoreroom esStoreroom : esStoreroomSet) {
                if (essSpace.getEssNo().equals(esStoreroom.getEssNo())){
                    esNo = esStoreroom.getEsNo();
                    existEsStoreroom = esStoreroom;
                    break;
                }
            }
            existEsStoreroom.setEssSpaceNumber(existEsStoreroom.getEssSpaceNumber()+1);
            essService.updateOne(existEsStoreroom);
            //补全库区信息并保存和更新Redis
            essSpace.setEsNo(esNo).setEsssFloorLocation(essSpace.getEsssFloorLocation() == null? 1 :essSpace.getEsssFloorLocation())
                            .setEsssTimeValue(SystemUtil.getTime()).setEsssTs(SystemUtil.getTime());
            save(essSpace);
            redisTemplate.opsForSet().add("essSpaces",essSpace);
        }else {
            throw new Exception("已存在该库区");
        }
    }

    @Override
    public void deleteOne(String esssId) {

    }

    @Override
    public void deleteMany(List<String> esssIdList) {

    }

    @Override
    public void updateOne(EssSpace essSpace) {

    }

    @Override
    public Page<EssSpace> selectByPage(Integer pageIndex, Integer pageSize) {
        return null;
    }

    @Override
    public Set<EssSpace> selectSetFromRedis() {
        //从redis中获取仓库数据，若存在直接返回，若不存在则先存入redis再返回
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        Set<Object> essSpaces = setOperations.members("essSpaces");
        Set<EssSpace> essSpaceSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(essSpaces)) {
            for (Object o : essSpaces) {
                essSpaceSet.add((EssSpace) o);
            }
        } else {
            for (EssSpace essSpace : list()) {
                setOperations.add("essSpaces",essSpace);
                essSpaceSet.add(essSpace);
            }
            redisTemplate.expire("essSpaces",30, TimeUnit.MINUTES);
        }
        return essSpaceSet;
    }

    public void updateCache(Set<EssSpace> essSpaces){
        redisTemplate.delete("essSpaces");
        for (EssSpace essSpace : essSpaces) {
            redisTemplate.opsForSet().add("essSpaces",essSpace);
        }
        redisTemplate.expire("essSpaces",30,TimeUnit.MINUTES);
    }
}
