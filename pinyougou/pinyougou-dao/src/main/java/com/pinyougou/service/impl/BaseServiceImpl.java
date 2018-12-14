package com.pinyougou.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

    //定义mapper
    @Autowired
    private Mapper<T> mapper;


    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 实体对象
     */
    @Override
    public T findOne(Serializable id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 查询全部
     *
     * @return 实体对象集合
     */
    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    /**
     * 根据条件查询列表
     *
     * @param t 查询条件对象
     * @return
     */
    @Override
    public List<T> findByWhere(T t) {
        return mapper.select(t);
    }

    /**
     * 分页查询列表
     *
     * @param page 页号 PageNum
     * @param rows 页大小 PageSize
     * @return 分页实体对象
     */
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        List<T> list = mapper.selectAll();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 根据条件分页查询列表
     *
     * @param page 页号 PageNum
     * @param rows 页大小 PageSize
     * @param t    查询条件对象
     * @return 分页实体对象
     */
    @Override
    public PageResult findPage(Integer page, Integer rows, T t) {
        PageHelper.startPage(page,rows);
        List<T> list = mapper.select(t);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    /**
     * 新增
     *
     * @param t 实体类对象
     */
    @Override
    public void add(T t) {
        mapper.insertSelective(t);
    }

    /**
     * 根据主键更新
     *
     * @param t 实体类对象
     */
    @Override
    public void update(T t) {
        mapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 批量删除
     *
     * @param ids 主键集合
     */
    @Override
    public void deleteByIds(Serializable[] ids) {
        if (ids!=null&&ids.length>0){
            for (Serializable id : ids) {
                mapper.deleteByPrimaryKey(id);
            }
        }
    }
}
