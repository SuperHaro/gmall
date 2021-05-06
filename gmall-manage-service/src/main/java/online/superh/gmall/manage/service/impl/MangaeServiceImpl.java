package online.superh.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import online.superh.gmall.bean.*;
import online.superh.gmall.config.RedisUtil;
import online.superh.gmall.manage.constant.ManageConst;
import online.superh.gmall.manage.mapper.*;
import online.superh.gmall.service.ManageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.persistence.Transient;
import java.util.List;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-23 13:34
 */
@Service
public class MangaeServiceImpl implements ManageService {
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 根据spuId获取所有spu图片
     *
     * @param spuImage
     * @return
     */
    @Override
    public List<SpuImage> getspuImageList(SpuImage spuImage) {

        return spuImageMapper.select(spuImage);
    }

    /**
     * 获取所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCatalog1> getCatalog1() {
        List<BaseCatalog1> baseCatalog1List = baseCatalog1Mapper.selectAll();
        return baseCatalog1List;
    }

    /**
     * 获取所有的二级分类
     *
     * @param catalog1Id
     * @return
     */
    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        return baseCatalog2Mapper.select(baseCatalog2);
    }

    /**
     * 获取所有的三级分类
     *
     * @param catalog2Id
     * @return
     */
    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);
    }

    /**
     * 根据三级分类获取平台属性
     *
     * @param catalog3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfo(String catalog3Id) {
//        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
//        baseAttrInfo.setCatalog3Id(catalog3Id);
//        return this.baseAttrInfoMapper.select(baseAttrInfo);
        List<BaseAttrInfo> baseAttrInfoList =baseAttrInfoMapper.getAttrInfoList(catalog3Id);
        return baseAttrInfoList;
    }

    /**
     * 根据平台属性ID获取平台属性值
     *
     * @param attrInfoId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValue(String attrInfoId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrInfoId);
        return baseAttrValueMapper.select(baseAttrValue);
    }

    /**
     * 添加平台属性
     *
     * @param baseAttrInfo
     */
    @Transactional
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //修改操作
        if(baseAttrInfo.getId()!=null|| baseAttrInfo.getId().length()>0){
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);

        }else {
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
            BaseAttrValue baseAttrValue2 = new BaseAttrValue();
            baseAttrValue2.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValue2);
            //保存value
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            if (attrValueList!=null&&attrValueList.size()>0) {
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    baseAttrValue.setAttrId(baseAttrInfo.getId());
                    baseAttrValueMapper.insertSelective(baseAttrValue);
                }

            }

    }

    /**
     * 根据平台属性Id获取平台属性
     *
     * @param attrInfoId
     */
    @Override
    public BaseAttrInfo getAttrInfoById(String attrInfoId) {
        BaseAttrInfo attrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrInfoId);
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrInfoId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);
        attrInfo.setAttrValueList(baseAttrValueList);
        return  attrInfo;
    }

    /**
     * 获取SpuInfo
     *
     * @param spuInfo
     * @return
     */
    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {

        return spuInfoMapper.select(spuInfo);
    }

    /**
     * 获取销售属性
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    /**
     * 保存spuInfo
     *
     * @param spuInfo
     */
    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insertSelective(spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList!=null&&spuImageList.size()>0){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(spuSaleAttrList!=null&&spuSaleAttrList.size()>0){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList!=null&&spuSaleAttrValueList.size()>0){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }

            }
        }


    }

    /**
     * 获取销售属性
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        //两张表关联查询
        List<SpuSaleAttr>  spuSaleAttrList =spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
        return spuSaleAttrList;
    }

    /**
     * 保存sku
     *
     * @param skuInfo
     */
    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        if(skuInfo!=null)
        {
            skuInfoMapper.insertSelective(skuInfo);
            List<SkuImage> skuImageList = skuInfo.getSkuImageList();
            if(skuImageList!=null&&skuImageList.size()>0){
                for (SkuImage skuImage : skuImageList) {
                    skuImage.setSkuId(skuInfo.getId());
                    skuImageMapper.insertSelective(skuImage);
                }
            }
            List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
            if(skuAttrValueList!=null&&skuAttrValueList.size()>0){
                for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                    skuAttrValue.setSkuId(skuInfo.getId());
                    skuAttrValueMapper.insertSelective(skuAttrValue);
                }
            }
            List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            if (skuSaleAttrValueList!=null&&skuSaleAttrValueList.size()>0){
                for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                    skuSaleAttrValue.setSkuId(skuInfo.getId());
                    skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
                }
            }
        }
    }

    /**
     * 根据skuid获取skuinfo
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            // 定义key
            String skuInfoKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX; //key= sku:skuId:info

            String skuJson = jedis.get(skuInfoKey);

            if (skuJson==null || skuJson.length()==0){
                // 没有数据 ,需要加锁！取出完数据，还要放入缓存中，下次直接从缓存中取得即可！
                System.out.println("没有命中缓存");
                // 定义key user:userId:lock
                String skuLockKey=ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKULOCK_SUFFIX;
                // 生成锁
                String lockKey  = jedis.set(skuLockKey, "OK", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                if ("OK".equals(lockKey)){
                    System.out.println("获取锁！");
                    // 从数据库中取得数据
                    skuInfo = getSkuInfoDB(skuId);
                    // 将是数据放入缓存
                    // 将对象转换成字符串
                    String skuRedisStr = JSON.toJSONString(skuInfo);
                    jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT,skuRedisStr);
                    jedis.del(skuLockKey);
                    return skuInfo;
                }else {
                    System.out.println("等待！");
                    // 等待
                    Thread.sleep(1000);
                    // 自旋
                    return getSkuInfo(skuId);
                }
            }else{
                // 有数据
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        // 从数据库返回数据
        return getSkuInfoDB(skuId);
//        try {
//            jedis = redisUtil.getJedis();
//            //获取数据
//            //判断缓存中是否有数据
//            String skuKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
//            if(jedis.exists(skuKey)){
//                String skuJson = jedis.get(skuKey);
//                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
//
//                return skuInfo;
//            }else {
//                skuInfo = getSkuInfoDB(skuId);
//                jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT,JSON.toJSONString(skuInfo));
//                return skuInfo;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(jedis!=null) {
//                jedis.close();
//            }
//        }
//        return getSkuInfoDB(skuId);
    }

    private SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);
        //获取SkuAttrValueList
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        return skuInfo;
    }

    /**
     * 获取销售属性回显并锁定
     *
     * @param skuInfo
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrlistChecked(SkuInfo skuInfo) {

        return spuSaleAttrMapper.getSpuSaleAttrlistChecked(skuInfo.getId(),skuInfo.getSpuId());
    }

    /**
     * 根据spuId查询销售属性值得集合
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SkuSaleAttrValue> getSpuSaleAttrValuelistBySpu(String spuId) {

        return skuSaleAttrValueMapper.selectSpuSaleAttrValuelistBySpu(spuId);
    }

    /**
     * 根据平台属性值Id
     *
     * @param attrValueIdList
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        //将集合变成字符串
        String valueIds = StringUtils.join(attrValueIdList.toArray(), ",");
        System.out.println(valueIds);
        return baseAttrInfoMapper.getAttrInfoListBy(valueIds);
    }
}

