package com.feng.boot.admin.project.system.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.feng.boot.admin.exceptions.LoginException;
import com.feng.boot.admin.project.system.menu.model.dto.MenuDTO;
import com.feng.boot.admin.project.system.menu.model.dto.TreeMenuDTO;
import com.feng.boot.admin.project.system.menu.model.entity.MenuEntity;
import com.feng.boot.admin.project.system.menu.model.query.MenuParams;
import com.feng.boot.admin.project.system.menu.model.vo.MenuMetaVO;
import com.feng.boot.admin.project.system.menu.model.vo.MenuPermissionVO;
import com.feng.boot.admin.project.system.menu.model.vo.VueMenuVO;
import com.feng.boot.admin.project.system.permission.mapper.IPermissionMapper;
import com.feng.boot.admin.project.system.permission.model.entity.PermissionEntity;
import com.feng.boot.admin.security.model.User;
import com.feng.boot.admin.security.utils.SecurityUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.feng.boot.admin.commons.constant.RedisConstant;
import com.feng.boot.admin.commons.enums.EnabledEnum;
import com.feng.boot.admin.commons.enums.ResponseStatusEnum;
import com.feng.boot.admin.commons.enums.SortTypeEnum;
import com.feng.boot.admin.domain.service.impl.SuperBaseServiceImpl;
import com.feng.boot.admin.event.menu.MenuEvent;
import com.feng.boot.admin.project.system.menu.mapper.IMenuMapper;
import com.feng.boot.admin.project.system.menu.service.IMenuService;
import com.feng.boot.admin.commons.cache.Cache;
import com.feng.boot.admin.commons.json.utils.Jsons;
import com.feng.boot.admin.commons.lang.StringUtils;
import com.feng.boot.admin.commons.lang.collection.CollectionUtils;
import com.feng.boot.admin.commons.spring.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.feng.boot.admin.commons.lang.constants.PathConst.ROOT_PATH;

/**
 * 菜单  服务实现类
 *
 * @author bing_huang
 * @since 3.0.0
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends SuperBaseServiceImpl<Long, MenuParams, MenuDTO, MenuEntity, IMenuMapper> implements IMenuService {
    private final IPermissionMapper permissionMapper;
    private final Cache<String, List<TreeMenuDTO>> redisCache;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MenuEntity entity) {
        Assert.notNull(entity, "修改信息不为空");
        Assert.notNull(entity.getId(), "id为空");
        Integer isEnabled = entity.getIsEnabled();
        boolean updateResult = super.updateById(entity);
        // 禁用子集
        if (EnabledEnum.UN_ENABLED.getValue().equals(isEnabled)) {
            List<MenuDTO> children = getChildrenByParenId(entity.getId());
            if (!CollectionUtils.isEmpty(children)) {
                Set<Long> childrenIds = children.stream().map(MenuDTO::getId).collect(Collectors.toSet());
                UpdateWrapper<MenuEntity> update = Wrappers.update();
                update.set(MenuEntity.IS_ENABLED, EnabledEnum.UN_ENABLED.getValue());
                update.in(MenuEntity.ID, childrenIds);
                super.update(update);
            }
        }
        return updateResult;
    }

    @Override
    public boolean removeById(Serializable id) {
        //获取子集
        Set<Long> ids = Sets.newHashSet((Long) id);
        List<MenuDTO> children = this.getChildrenByParenId((Long) id);
        if (!CollectionUtils.isEmpty(children)) {
            Set<Long> childrenIds = children.stream().map(MenuDTO::getId).collect(Collectors.toSet());
            ids.addAll(childrenIds);
        }
        return super.removeByIds(ids);
    }

    @Override
    @Nullable
    public List<MenuDTO> getChildrenByParenId(@Nonnull Long id) {
        Assert.notNull(id, "id不为空");
        List<MenuEntity> entities = super.list();
        List<MenuDTO> menu = BeanUtils.transformFromInBatch(entities, MenuDTO.class);
        List<MenuDTO> result = Lists.newArrayList();
        for (MenuDTO dto : menu) {
            // 第一级
            if (dto.getParentId().equals(id)) {
                result.add(dto);
                for (MenuDTO item : menu) {
                    if (dto.getId().equals(item.getParentId())) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    @SneakyThrows
    @Override
    public List<TreeMenuDTO> getCurrentMenu() {
        User currentUser = SecurityUtils.getCurrentUser();
        if (null == currentUser) {
            throw new LoginException(ResponseStatusEnum.USE_LOGIN_ERROR, "当前用户未登录,请登录后重试");
        }
        Optional<List<TreeMenuDTO>> optional = redisCache.get(RedisConstant.MENU_KEY_PREFIX + currentUser.getId());
        if (!optional.isPresent()) {
            eventPublisher.publishEvent(new MenuEvent(this, currentUser.getId()));
        }
        optional = redisCache.get(RedisConstant.MENU_KEY_PREFIX + currentUser.getId());

        if (!optional.isPresent()) {
            return Lists.newArrayList();
        }
        return Jsons.JSONS.jsonToList(Jsons.JSONS.objectToJson(optional.get()), TreeMenuDTO.class);
    }

    @Override
    public boolean updateCurrentMenu() {
        User currentUser = SecurityUtils.getCurrentUser();
        if (null == currentUser) {
            throw new LoginException(ResponseStatusEnum.USE_LOGIN_ERROR, "当前用户未登录,请登录后重试");
        }
        eventPublisher.publishEvent(new MenuEvent(this, currentUser.getId()));
        return true;
    }

    @Override
    public List<TreeMenuDTO> queryTree() {
        MenuParams params = new MenuParams();
        params.setSortColumn(Collections.singletonList(MenuEntity.SORT));
        params.setSortType(SortTypeEnum.ASC.getValue());
        QueryWrapper<MenuEntity> query = super.query(params);
        List<MenuEntity> entities = super.list(query);
        List<TreeMenuDTO> treeMenu = BeanUtils.transformFromInBatch(entities, TreeMenuDTO.class);
        return buildTree(treeMenu);
    }

    @Override
    public List<TreeMenuDTO> buildTree(List<TreeMenuDTO> menu) {
        List<TreeMenuDTO> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (TreeMenuDTO treeMenu : menu) {
            // -1或者null
            if (treeMenu.getParentId() == null || treeMenu.getParentId() == -1) {
                trees.add(treeMenu);
            }
            for (TreeMenuDTO it : menu) {
                if (treeMenu.getId().equals(it.getParentId())) {
                    if (treeMenu.getChildren() == null) {
                        treeMenu.setChildren(new ArrayList<>());
                    }
                    treeMenu.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        if (trees.size() == 0) {
            trees = menu.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        }
        return trees;
    }

    @Override
    public List<VueMenuVO> buildVueMenus(List<TreeMenuDTO> treeMenu) {
        List<VueMenuVO> list = new LinkedList<>();
        treeMenu.forEach(menu -> {
                    if (menu != null) {
                        List<TreeMenuDTO> menuDtoList = menu.getChildren();
                        // 前端路由
                        VueMenuVO menuVo = new VueMenuVO();
                        // 一级目录需要加斜杠，不然会报警告 path
                        if (!menu.getPath().startsWith(ROOT_PATH)
                                &&
                                (menu.getParentId() == null || menu.getParentId() == -1)) {
                            menuVo.setPath(ROOT_PATH + menu.getPath());
                        } else {
                            menuVo.setPath(menu.getPath());
                        }
                        // 组件名称 name
                        menuVo.setName(menu.getEnname());
                        //是否隐藏
                        menuVo.setHidden(false);
                        // component
                        if (menu.getParentId() == null || menu.getParentId() == -1) {
                            // 设置展开类型 默认 侧边栏
                            menuVo.setComponent(StringUtils.isEmpty(menu.getComponent()) ? "Layout" : menu.getComponent());
                        } else if (!StringUtils.isEmpty(menu.getComponent())) {
                            menuVo.setComponent(menu.getComponent());
                        }
                        // vue router meta
                        menuVo.setMeta(new MenuMetaVO(menu.getTitle(), menu.getIcon(), false, true));

                        if (menuDtoList != null && menuDtoList.size() != 0) {
                            menuVo.setAlwaysShow(true);
                            menuVo.setRedirect("noredirect");
                            menuVo.setChildren(buildVueMenus(menuDtoList));
                            // 处理是一级菜单并且没有子菜单的情况
                        } else if (menu.getParentId() == null || menu.getParentId() == -1) {
                            VueMenuVO menuVo1 = new VueMenuVO();
                            menuVo1.setMeta(menuVo.getMeta());
                            menuVo1.setName(menuVo.getName());
                            menuVo1.setComponent(menuVo.getComponent());
                            menuVo1.setPath(menu.getPath());

                            menuVo.setName(null);
                            menuVo.setMeta(null);
                            menuVo.setComponent("Layout");
                            List<VueMenuVO> list1 = new ArrayList<>();
                            list1.add(menuVo1);
                            menuVo.setChildren(list1);
                        }
                        list.add(menuVo);
                    }
                }
        );
        return list;
    }

    @Override
    public List<MenuPermissionVO> queryMenuPermissionTree() {
        List<PermissionEntity> permissionEntities = permissionMapper.selectList(null);
        List<MenuEntity> entities = super.list();
        List<MenuPermissionVO> trees = new ArrayList<>();
        for (MenuEntity menuEntity : entities) {

            MenuPermissionVO menuPermission = new MenuPermissionVO();
            if (menuEntity.getParentId() == null || menuEntity.getParentId() == -1) {
                menuPermission.setId(menuEntity.getId());
                menuPermission.setName(menuEntity.getTitle());
                menuPermission.setIsPermission(false);
                trees.add(menuPermission);
            }
            for (MenuEntity entity : entities) {
                if (menuEntity.getId().equals(entity.getParentId())) {
                    if (menuPermission.getChildren() == null) {
                        menuPermission.setChildren(new ArrayList<>());
                    }
                    MenuPermissionVO menuPermissionInfo = new MenuPermissionVO();
                    menuPermissionInfo.setName(entity.getTitle());
                    menuPermissionInfo.setId(entity.getId());
                    menuPermissionInfo.setIsPermission(false);
                    menuPermission.getChildren().add(menuPermissionInfo);
                    // 权限
                    List<PermissionEntity> permissionList = permissionEntities.stream().filter(e -> e.getMenuId().equals(entity.getId())).collect(Collectors.toList());
                    for (PermissionEntity permissionEntity : permissionList) {
                        MenuPermissionVO permission = new MenuPermissionVO();
                        permission.setIsPermission(true);
                        permission.setId(permissionEntity.getId());
                        permission.setName(permissionEntity.getName());
                        if (menuPermissionInfo.getChildren() == null) {
                            menuPermissionInfo.setChildren(new ArrayList<>());
                        }
                        menuPermissionInfo.getChildren().add(permission);
                    }
                }
            }
        }
        return trees;
    }

    @Override
    public List<MenuEntity> getSuperior(@Nonnull Long id, List<MenuEntity> entities) {
        if (null == id || -1 == id) {
            return entities;
        }
        MenuEntity menuEntity = super.getById(id);
        entities.add(menuEntity);
        return getSuperior(menuEntity.getParentId(), entities);
    }
}
