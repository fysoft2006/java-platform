package com.whenling.core.web.menu;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.whenling.core.model.Menu;
import com.whenling.core.service.MenuService;
import com.whenling.core.support.entity.Result;
import com.whenling.core.support.entity.Tree;
import com.whenling.core.web.menu.vo.MenuVo;

@Controller
@RequestMapping("/admin/menu")
public class MenuController {

	@Autowired
	private MenuService menuService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public Page<Menu> list(Pageable pageable) {
		return menuService.findAll(pageable);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Result save(@ModelAttribute @Valid MenuVo menuVo, BindingResult bindingResult,
			@RequestParam(value = "current") Menu current, Model model) {
		if (bindingResult.hasErrors()) {
			return Result.validateError(bindingResult.getAllErrors());
		}

		Menu menu = menuVo.getId() == null ? menuService.newEntity() : menuService.findOne(menuVo.getId());
		if (menu.isNew()) {
			menu.setParent(current);
		}
		menuVo.applyTo(menu);
		menuService.save(menu);

		return Result.success();
	}

	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	@ResponseBody
	public Tree<Menu> tree() {
		Tree<Menu> tree = menuService.findTree(null);
		return tree;
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	public Menu get(@RequestParam(value = "id", required = false) Menu menu) {

		return menu == null ? menuService.newEntity() : menu;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST, params = "id")
	@ResponseBody
	public Result delete(@RequestParam(value = "id") Menu menu) {
		menuService.delete(menu);
		return Result.success();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST, params = "ids")
	@ResponseBody
	public Result batchDelete(@RequestParam(value = "ids") Menu[] menus) {
		for (Menu menu : menus) {
			menuService.delete(menu);
		}

		return Result.success();
	}
}
