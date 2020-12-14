package com.feng.boot.admin.project.system.user.model.dto;

import com.feng.boot.admin.project.system.user.model.entity.UserInfoEntity;
import com.feng.boot.admin.domain.model.InputConverter;
import com.feng.boot.admin.domain.model.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

/**
 * 用户信息
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserInfoDTO extends BaseDTO implements InputConverter<UserInfoEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * 备注
     */
    private String description;

    /**
     * id
     */
    private Long id;
    /**
     * 用户账号
     */
    @NotBlank(message = "用户账号不为空")
    private String username;
    /**
     * 昵称
     */
    @NotBlank(message = "用户名不为空")
    private String nickName;

    /**
     * 用户手机号
     */
    private String phoneNumber;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否为管理员
     */
    private Integer isAdmin = 0;
    /**
     * 所属组织
     */
    private Long deptId;
    /**
     * 角色id
     */
    private Collection<Long> roleIds;
    /**
     * 岗位id
     */
    private Collection<Long> postIds;

    /**
     * 权限id
     */
    private Collection<Long> permissionIds;

}
