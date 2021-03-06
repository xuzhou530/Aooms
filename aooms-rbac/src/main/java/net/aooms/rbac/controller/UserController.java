package net.aooms.rbac.controller;

import net.aooms.core.Aooms;
import net.aooms.core.web.AoomsAbstractController;
import net.aooms.rbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 * Created by 风象南(yuboon) on 2018-09-12
 */
@RestController
@RequestMapping(Aooms.WebContext + "/rbac/userService")
public class UserController extends AoomsAbstractController {

    @Autowired
    private UserService userService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("/findList")
    public void findList(){
        userService.findList();
    };

    /**
     * 查询用户拥有的资源
     * @return
     */
    @RequestMapping("/findResourceByUserId")
    public void findResourceByUserId(){
        userService.findResourceByUserId();
    };

    /**
     * 新增
     * @return
     */
    @PostMapping("/insert")
    public void insert(){
        userService.insert();
    };

    /**
     * 修改
     * @return
     */
    @PostMapping("/update")
    public void update(){
        userService.update();
    };

    /**
     * 修改状态
     * @return
     */
    @PostMapping("/updateStatus")
    public void updateStatus(){
        userService.updateStatus();
    };

    /**
     * 删除
     * @return
     */
    @PostMapping("/delete")
    public void delete(){
        userService.delete();
    };

    /**
     * 查询拥有的角色
     * @return
     */
    @RequestMapping("/findRoleByUserId")
    public void findRoleByUserId(){
        userService.findRoleByUserId();
    };


}