package com.feng.boot.admin.project.system.role.controller;


import com.feng.boot.admin.annotation.ClassDescribe;
import com.feng.boot.admin.annotation.Log;
import com.feng.boot.admin.annotation.PreAuth;
import com.feng.boot.admin.commons.enums.BusinessTypeEnum;
import com.feng.boot.admin.domain.controller.SuperSimpleBaseController;
import com.feng.boot.admin.domain.result.Result;
import com.feng.boot.admin.domain.result.R;
import com.feng.boot.admin.project.system.role.model.dto.RoleExtDTO;
import com.feng.boot.admin.project.system.role.model.entity.RoleEntity;
import com.feng.boot.admin.project.system.role.model.query.RoleParams;
import com.feng.boot.admin.project.system.role.service.IRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色  前端控制器
 *
 * @author bing_huang
 * @since 3.0.0
 */
@RestController
@RequestMapping("/api/v3/system/role")
@PreAuth("role")
@ClassDescribe("角色管理")
public class RoleController extends SuperSimpleBaseController<Long, RoleExtDTO, RoleParams, RoleEntity> {
    private final IRoleService service;

    public RoleController(IRoleService service) {
        super(service);
        this.service = service;
    }

    /**
     * 修改角色分配的菜单
     *
     * @param id            角色id
     * @param permissionIds 权限id
     * @return 是否成功
     */
    @PostMapping("/update/permission/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR','role:permission:save')")
    @Log(value = "分配权限", paramsName = {"permissionIds"}, businessType = BusinessTypeEnum.UPDATE)
    public Result<String> updateRolePermission(@PathVariable("id") Long id, @RequestBody List<Long> permissionIds) {
        service.updateRolePermission(id, permissionIds);
        return R.success("保存成功");
    }
}

