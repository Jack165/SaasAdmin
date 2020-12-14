package com.feng.boot.admin.project.system.dept.controller;


import com.feng.boot.admin.annotation.ClassDescribe;
import com.feng.boot.admin.annotation.PreAuth;
import com.feng.boot.admin.domain.controller.SuperSimpleBaseController;
import com.feng.boot.admin.domain.result.Result;
import com.feng.boot.admin.domain.result.R;
import com.feng.boot.admin.project.system.dept.model.dto.DeptDTO;
import com.feng.boot.admin.project.system.dept.model.dto.TreeDeptDTO;
import com.feng.boot.admin.project.system.dept.model.entity.DeptEntity;
import com.feng.boot.admin.project.system.dept.model.query.DeptParams;
import com.feng.boot.admin.project.system.dept.service.IDeptService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 部门  前端控制器
 *
 * @author bing_huang
 * @since 3.0.0
 */
@RestController
@RequestMapping("/api/v3/system/dept")
@ClassDescribe("部门管理")
@PreAuth("dept")
public class DeptController extends SuperSimpleBaseController<Long, DeptDTO, DeptParams, DeptEntity> {
    private final IDeptService service;

    public DeptController(IDeptService service) {
        super(service);
        this.service = service;
    }

    /**
     * 获取全部树形组织
     *
     * @return 树形组织
     */
    @GetMapping("/tree/all")
    public Result<Set<TreeDeptDTO>> getDeptTreeAll() {
        DeptParams params = new DeptParams();
        List<DeptDTO> list = service.list(params);
        Set<TreeDeptDTO> treeDept = service.buildTree(list);
        return R.success(treeDept);
    }

}

