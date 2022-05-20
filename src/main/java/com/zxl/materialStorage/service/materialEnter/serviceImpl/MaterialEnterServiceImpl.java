package com.zxl.materialStorage.service.materialEnter.serviceImpl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.materialStorage.mapper.materialEnter.MaterialEnterMapper;
import com.zxl.materialStorage.model.pojo.*;
import com.zxl.materialStorage.service.materialEnter.MaterialAttributeService;
import com.zxl.materialStorage.service.materialEnter.MaterialEnterService;
import com.zxl.materialStorage.service.materialEnter.MaterialPackingService;
import com.zxl.materialStorage.service.materialEnter.MaterialTypeService;
import com.zxl.materialStorage.service.storageManage.EssService;
import com.zxl.materialStorage.service.storageManage.EsssService;
import com.zxl.materialStorage.service.storageManage.EssssService;
import com.zxl.materialStorage.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @className: MaterialEnterServiceImpl
 * @description: MaterialEnterServiceImpl
 * @author: ZhangXiaolei
 * @date: 2022/5/4
 **/
@Service
public class MaterialEnterServiceImpl extends ServiceImpl<MaterialEnterMapper, MaterialEnter> implements MaterialEnterService {
    @Autowired
    private EssssService essssService;

    @Autowired
    private EsssService esssService;

    @Autowired
    private EssService essService;

    @Autowired
    private MaterialTypeService materialTypeService;

    @Autowired
    private MaterialPackingService materialPackingService;

    @Autowired
    private MaterialAttributeService materialAttributeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNewOne(MaterialEnter materialEnter) throws Exception {
        //校验
        MaterialEnter existMaterialEnter = getOne(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmeNo, materialEnter.getEmeNo()));
        if (ObjectUtil.isNotNull(existMaterialEnter)) {
            throw new Exception("已存在此编号的入库物资");
        }
        //补全
        Double priceCount = materialEnter.getEmePriceUnit() * materialEnter.getEmeNumberCount();
        EsssShelves esssShelves = essssService.getOne(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssssNo, materialEnter.getEssssNo()));
        materialEnter.setEsNo(esssShelves.getEsNo()).setEssNo(esssShelves.getEsssNo()).setEsssNo(esssShelves.getEsssNo())
                .setEmePriceCount(priceCount).setEmeTimeValue(SystemUtil.getTime()).setEmeTs(SystemUtil.getTime());
        save(materialEnter);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String emeId) {
        removeById(emeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMany(List<String> emeIdList) {
        removeByIds(emeIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(MaterialEnter materialEnter) {
        Double priceCount = materialEnter.getEmePriceUnit() * materialEnter.getEmeNumberCount();
        EsssShelves esssShelves = essssService.getOne(new QueryWrapper<EsssShelves>().lambda().eq(EsssShelves::getEssssNo, materialEnter.getEssssNo()));
        materialEnter.setEsNo(esssShelves.getEsNo()).setEssNo(esssShelves.getEssNo()).setEsssNo(esssShelves.getEsssNo())
                .setEmePriceCount(priceCount).setEmeTs(SystemUtil.getTime());
        updateById(materialEnter);
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::isEmeIsAccept, true));
        redisTemplate.delete("materialEnters");
        for (MaterialEnter enter : materialEnterList) {
            redisTemplate.opsForSet().add("materialEnters", enter);
        }
        redisTemplate.expire("materialEnters", 120, TimeUnit.MINUTES);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNos(String esNoOld, String esNoNew) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEsNo, esNoOld));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEmtNo(esNoNew).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEmtNos(MaterialType byId, MaterialType materialType) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmtNo, byId.getEmtNo()));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEmtNo(materialType.getEmtNo()).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEmpNos(MaterialPacking byId, MaterialPacking materialPacking) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmpNo, byId.getEmpNo()));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEmpNo(materialPacking.getEmpNo()).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEmaNos(MaterialAttribute byId, MaterialAttribute materialAttribute) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEmaNo, byId.getEmaNo()));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEmaNo(materialAttribute.getEmaNo()).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    public List<MaterialEnter> selectAll() {
        return list();
    }

    @Override
    public List<MaterialEnter> selectAlready() {
        return list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::isEmeIsAccept, false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void materialAccept(MaterialEnter materialEnter) {
        materialEnter.setEmeIsAccept(true).setEmeTs(SystemUtil.getTime());
        updateById(materialEnter);
    }

    @Override
    public Map<String, List<String>> getAllNeedInfo() {
        Map<String, List<String>> map = new HashMap<>();
        List<EsssShelves> shelvesList = essssService.list();
        List<String> essssNoList = new ArrayList<>();
        for (EsssShelves esssShelves : shelvesList) {
            essssNoList.add(esssShelves.getEssssNo());
        }
        map.put("essssNos", essssNoList);

        List<MaterialType> materialTypeList = materialTypeService.list();
        List<String> emtNoList = new ArrayList<>();
        for (MaterialType materialType : materialTypeList) {
            emtNoList.add(materialType.getEmtNo());
        }
        map.put("emtNos", emtNoList);

        List<MaterialPacking> materialPackingList = materialPackingService.list();
        List<String> empNoList = new ArrayList<>();
        for (MaterialPacking materialPacking : materialPackingList) {
            empNoList.add(materialPacking.getEmpNo());
        }
        map.put("empNos", empNoList);

        List<MaterialAttribute> materialAttributeList = materialAttributeService.list();
        List<String> emaNoList = new ArrayList<>();
        List<String> emaNameList = new ArrayList<>();
        for (MaterialAttribute materialAttribute : materialAttributeList) {
            emaNoList.add(materialAttribute.getEmaNo());
            emaNameList.add(materialAttribute.getEmaName());
        }
        map.put("emaNos", emaNoList);
        map.put("emaNames", emaNameList);

        return map;
    }

    @Override
    public Set<MaterialEnter> selectAccept() {
        //利用缓存
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        Set<Object> materialEnters = setOperations.members("materialEnters");
        Set<MaterialEnter> materialEnterSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(materialEnters)) {
            for (Object materialEnter : materialEnters) {
                materialEnterSet.add((MaterialEnter) materialEnter);
            }
        } else {
            List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::isEmeIsAccept, true));
            for (MaterialEnter materialEnter : materialEnterList) {
                setOperations.add("materialEnters", materialEnter);
                materialEnterSet.add(materialEnter);
            }
            redisTemplate.expire("materialEnters", 120, TimeUnit.MINUTES);
        }

        return materialEnterSet;
    }

    @Override
    public void updateEsNosByDel(List<String> esNoList) {
        LinkedList<MaterialEnter> materialEnters = new LinkedList<>();
        for (String esNo : esNoList) {
            List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEsNo, esNo));
            for (MaterialEnter materialEnter : materialEnterList) {
                materialEnter.setEsNo(null).setEmeTs(SystemUtil.getTime());
            }
            materialEnters.addAll(materialEnterList);
        }
        updateBatchById(materialEnters);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEssNos(String essNoOld, String essNoNew) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEssNo, essNoOld));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEssNo(essNoNew).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNosWithEssNo(String esNoOld, String esNoNew, String essNo) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEssNo, essNo).eq(MaterialEnter::getEsNo, esNoOld));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEsNo(esNoNew).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsssNos(String esssNoOld, String esssNoNew) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEsssNo, esssNoOld));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEsssNo(esssNoNew).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEssNosAndEsNosWithEsssNo(String essNoOld, String essNoNew, String esssNo) {
        EsStoreroom esStoreroom = essService.getOne(new QueryWrapper<EsStoreroom>().lambda().eq(EsStoreroom::getEssNo, essNoNew));
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEssNo, essNoOld).eq(MaterialEnter::getEsssNo, esssNo));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEsNo(esStoreroom.getEsNo()).setEssNo(essNoNew).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEsNosAndEssNosAndEsssNosWithEssssNo(String esssNoOld, String esssNoNew, String essssNo) {
        EssSpace essSpace = esssService.getOne(new QueryWrapper<EssSpace>().lambda().eq(EssSpace::getEsssNo, esssNoNew));
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEsssNo, esssNoOld).eq(MaterialEnter::getEssssNo, essssNo));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEsNo(essSpace.getEsNo()).setEssNo(essSpace.getEssNo()).setEsssNo(essSpace.getEsssNo()).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Async("threadPool")
    public void updateEssssNos(String essssNoOld, String essssNoNew) {
        List<MaterialEnter> materialEnterList = list(new QueryWrapper<MaterialEnter>().lambda().eq(MaterialEnter::getEssssNo, essssNoOld));
        for (MaterialEnter materialEnter : materialEnterList) {
            materialEnter.setEssssNo(essssNoNew).setEmeTs(SystemUtil.getTime());
        }
        updateBatchById(materialEnterList);
    }
}
