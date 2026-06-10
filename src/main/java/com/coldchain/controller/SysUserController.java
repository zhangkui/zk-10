package com.coldchain.controller;

import com.coldchain.common.Result;
import com.coldchain.entity.SysUser;
import com.coldchain.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @ApiParam("用户名") @RequestParam String username,
            @ApiParam("密码") @RequestParam String password,
            HttpServletRequest request) {
        List<SysUser> users = sysUserService.list();
        for (SysUser user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                if (user.getStatus() == 0) {
                    return Result.error("账号已被禁用");
                }
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setMaxInactiveInterval(3600);
                Map<String, Object> data = new HashMap<>();
                data.put("id", user.getId());
                data.put("username", user.getUsername());
                data.put("realName", user.getRealName());
                data.put("role", user.getRole());
                data.put("phone", user.getPhone());
                data.put("token", session.getId());
                return Result.success(data);
            }
        }
        return Result.error("用户名或密码错误");
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<Boolean> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Result.success(true);
    }

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/current")
    public Result<SysUser> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(user);
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        return Result.success(sysUserService.list());
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/detail")
    public Result<SysUser> getById(@ApiParam("用户ID") @RequestParam Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @ApiOperation("新增用户")
    @PostMapping("/add")
    public Result<Boolean> save(@RequestBody SysUser user) {
        boolean result = sysUserService.save(user);
        return result ? Result.success(result) : Result.error("新增失败");
    }

    @ApiOperation("修改用户")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysUser user) {
        boolean result = sysUserService.update(user);
        return result ? Result.success(result) : Result.error("修改失败");
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@ApiParam("用户ID") @RequestParam Long id) {
        boolean result = sysUserService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }
}
