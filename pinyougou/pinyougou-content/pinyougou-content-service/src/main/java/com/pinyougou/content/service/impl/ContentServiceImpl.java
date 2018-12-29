package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    private static final String REDIS_CONTENT = "content";
    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增
     *
     * @param tbContent 实体类对象
     */
    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //更新内容分类对应在redis中的内容列表缓存
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    private void updateContentInRedisByCategoryId(Long categoryId) {
        try {
            redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据主键更新
     *
     * @param tbContent 实体类对象
     */
    @Override
    public void update(TbContent tbContent) {
        TbContent oldContent = super.findOne(tbContent.getId());
        super.update(tbContent);
        //是否修改了内容分类,如果修改了内容分类则需要将新旧分类对应的内容列表都更新
        //如果旧的内容分类里面的分类id不包含新内容分类里面的分类id
        if (!oldContent.getCategoryId().equals(tbContent.getCategoryId())){
        updateContentInRedisByCategoryId(oldContent.getCategoryId());
        }
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 批量删除
     *
     * @param ids 主键集合
     */
    @Override
    public void deleteByIds(Serializable[] ids) {
        //1.根据内容id集合查询内容列表,然后在更新该内容分类对应的内容列表缓存
        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));

        List<TbContent> contentList = contentMapper.selectByExample(example);
        if (contentList!=null&&contentList.size()>0){
            for (TbContent tbContent : contentList) {
                updateContentInRedisByCategoryId(tbContent.getCategoryId());
            }
        }
        //2.删除内容
        super.deleteByIds(ids);
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> list =null;
        try {
            //先从缓存中查找 没有的话执行try catch之后的代码
            list = (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT).get(categoryId);
            if (list!=null){
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId",categoryId);
        //启用状态的才查询
        criteria.andEqualTo("status","1");
        //降序排序
        example.orderBy("sortOrder").desc();

         list = contentMapper.selectByExample(example);
         //缓存中没有的话 存储到缓存中 下次直接从缓存中拿.

         try {
             //设置某个分类对应的广告内容列表到缓存中
             redisTemplate.boundHashOps(REDIS_CONTENT).put(categoryId,list);
         } catch (Exception e){
             e.printStackTrace();
         }
        return list;
    }
}
