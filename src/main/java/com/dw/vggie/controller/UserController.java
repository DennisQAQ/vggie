package com.dw.vggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dw.vggie.common.R;
import com.dw.vggie.entities.User;
import com.dw.vggie.service.UserService;
import com.dw.vggie.utils.SMSUtils;
import com.dw.vggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户登录
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    /**
     * 发送短信验证
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取前端传递的手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生产4位随机的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //调用阿里云短信平台api发送短语
            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
            //将验证吗存入session
            session.setAttribute(phone,code);

            return R.success("短信验证码发送成功！");
        }
        return R.error("短信验证码发送失败！");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //1.获取手机号
        String phone = map.get("phone").toString();
        //2.获取验证码
        String code = map.get("code").toString();
//        map.get("name").toString();
        //3.从session中获取验证码
        Object codeInSession = session.getAttribute(phone);
        //4.进行验证码比对，（页面提交的验证码和session中保存的验证码比对）
        if(codeInSession!=null&&codeInSession.equals(code)){
            //5.如果比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User users = userService.getOne(queryWrapper);
            //6.判断当前用户是否是新用户，如果是新用户则自动完成注册
            if (users ==null){
                 users = new User();
                users.setPhone(phone);

                users.setStatus(1);
                userService.save(users);
            }
            session.setAttribute("user",users.getId());
            return R.success(users);
        }
        return R.error("登录失败");
    }

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("用户注销成功！返回登陆页面");
    }
}
