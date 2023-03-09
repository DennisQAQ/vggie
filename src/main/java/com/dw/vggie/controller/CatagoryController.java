package com.dw.vggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dw.vggie.common.R;
import com.dw.vggie.entities.Category;
import com.dw.vggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理

 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CatagoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *  * 在开发代码之前，需要梳理一下整个程序的执行过程:
     *  * 1、页面(backend/page/category/list.html)发送ajax请求，将新增分类窗口输入的数据以jslon形式提交到服务端2、服务端Controller接收页面提交的数据并调用Service将数据进行保存
     *  * 3、Service调用Mapper操作数据库，保存数据
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增分类",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     *
     *  1、页面(backend/page/category/list.html)发送ajax请求，将新增分类窗口输入的数据以jslon形式提交到服务端
     *  2、服务端Controller接收页面提交的数据并调用Service将数据进行保存
     *  3、Service调用Mapper操作数据库，保存数据
     * @param page
     * @param pageSize
     * @return R
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page<Category> pageInfo =new Page<>();
        LambdaQueryWrapper<Category>  queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 1、页面发送ajax请求，将参数(id)提交到服务端
     * 2、服务端Controller接收页面提交的数据并调用Service删除数据3、Service调用Mapper操作数据库
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteId(Long ids){
        log.info("删除分类，id为",ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");

    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("根据id修改分类信息{}",category);

        categoryService.updateById(category);
        return R.success("修改分类信息成功！");
    }



    @GetMapping("/list")
    public R<List<Category>> listByCategory(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
