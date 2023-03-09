package com.dw.vggie.controller;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dw.vggie.common.R;
import com.dw.vggie.entities.Employee;
import com.dw.vggie.service.EmployeeService;
import com.dw.vggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeContrller {
    @Autowired
    private EmployeeService employeeService = new EmployeeServiceImpl();
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 用户点击页面中退出按钮，发送请求，请求地址为/employee/logout，请求方式为POST。我们只需要在Controller中创建对应的处理方法即可，具体的处理逻辑:
     * 1、清理Session中的用户id
     * 2、返回结果
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 在开发代码之前，需要梳理一下整个程序的执行过程:
     * 1、页面发送ajax请求，将新增员工页面中输入的数据以json的形式提交到服务端
     * 2、服务端Controller接收页面提交的数据并调用Service将数据进行保存
     * 3、Service调用Mapper操作数据库，保存数据
     */
    @PostMapping
    public R<String> save (@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工"+employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employeeService.save(employee);

        return R.success("新增员工成功");


    }

        /*
        员工信息分页查询
        1、页面发送ajax请求,将分页查询参数(page、pageSize. name)提交到服务端
        2、服务端Controller接收页面提交的数据并调用Service查询数据
        3、Service调 用Mapper操作数据库,查询分页数据
        4、Controller将 查询到的分页数据响应给页面
        5、页面接收到分页数据并通过ElementUl的Table组件展示到页面.上
         */
    @GetMapping("/page")
    public  R<Page> pageR(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //分页构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        AbstractWrapper like = lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询条件
        employeeService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据ID修改员工信息
     * 在开发代码之前，需要梳理一下整个程序的执行过程:
     * 1、页面发送ajax请求，将参数(id、 status)提交到服务端
     * 2、服务端Controller接收页面提交的数据并调用Service更新数据
     * 3、Service调用Mapper操作数据库
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        log.info(employee.toString());

        employeeService.updateById(employee);
        return R.success("员工信息更新成功！");
    }

    /**
     * 在开发代码之前需要梳理一下操作过程和对应的程序的执行流程:
     *       1、点击编辑按钮时，页面跳转到add.html，并在url中携带参数[员工id]2、在add.html页面获取url中的参数[员工id]
     *       3、发送ajax请求，请求服务端，同时提交员工id参数
     *       4、服务端接收请求，根据员工id查询员工信息，将员工信息以json形式响应给页面5、页面接收服务端响应的json数据，通过VUE的数据绑定进行员工信息回显
     *       6、点击保存按钮，发送ajax请求，将页面中的员工信息以json方式提交给服务端7、服务端接收员工信息，并进行处理，完成后给页面响应
     *       8、页面接收到服务端响应信息后进行相应处理
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> updateById(@PathVariable Long id){
        log.info("根据id查询员工信息！");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应的员工信息");
    }

}
