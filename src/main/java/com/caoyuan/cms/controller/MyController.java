package com.caoyuan.cms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.domain.Collect;
import com.caoyuan.cms.domain.Comment;
import com.caoyuan.cms.domain.User;
import com.caoyuan.cms.service.ArticleService;
import com.caoyuan.cms.service.CollectService;
import com.caoyuan.cms.service.CommentService;
import com.caoyuan.cms.utils.ArticleEnum;
import com.caoyuan.cms.utils.Result;
import com.caoyuan.cms.utils.ResultUtil;
import com.caoyuan.cms.vo.ArticleVO;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;

/**
 * @ClassName: MyController
 * @Description: 注册用户的个人中心
 * @author: 曹原
 * @param <E>
 * @date: 2019年11月14日 上午11:06:11
 */
@RequestMapping("my")
@Controller
public class MyController {

	@Resource
	private ArticleService articleService;

	@Resource
	private CollectService collectService;// 我的收藏

	@Resource
	private CommentService commentService;

	/**
	 * 
	 * @Title: index
	 * @Description: 去个人中心首页
	 * @return
	 * @return: String
	 */
	@RequestMapping(value = { "", "/", "index" })
	public String index() {

		return "my/index";

	}

	/**
	 * 
	 * @Title: collects
	 * @Description: 我的收藏
	 * @return
	 * @return: String
	 */
	@GetMapping("collects")
	public String collects(HttpServletRequest request, Model model,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "3") Integer pageSize) {
		HttpSession session = request.getSession(false);

		User user = (User) session.getAttribute("user");

		PageInfo<Collect> info = collectService.selects(page, pageSize, user);

		model.addAttribute("info", info);
		return "/my/collect/collects";

	}

	/**
	 * 
	 * @Title: deleteCollect 
	 * @Description: 移除收藏
	 * @param id
	 * @return
	 * @return: Result<Collect>
	 */
	@ResponseBody
	@PostMapping("deleteCollect")
	public Result<Collect> deleteCollect(Integer id) {
		collectService.deleteById(id);
		return ResultUtil.success();
	}

	/**
	 * 
	 * @Title: detail
	 * @Description: 查询文章详情
	 * @param id
	 * @return
	 * @return: String
	 */
	@RequestMapping("article")
	public String detail(Model model, Integer id) {
		ArticleWithBLOBs article = articleService.selectByPrimaryKey(id);
		model.addAttribute("article", article);
		if (article.getContentType() == 0) {
			return "/my/article/article";
		} else {
			String string = article.getContent();
			List<ArticleVO> list = JSON.parseArray(string, ArticleVO.class);
			model.addAttribute("title", article.getTitle());// 标题
			model.addAttribute("list", list);// 标题包含的 图片的地址和描述
			return "/my/article/articlepic";
		}
	}

	/**
	 * 返回我的文章
	 * 
	 * @Title: selectByUser
	 * @Description: 
	 * @param model
	 * @param page
	 * @param pageSize
	 * @return
	 * @return: String
	 */
	@GetMapping("selectByUser")
	public String selectByUser(HttpServletRequest request, Model model,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "3") Integer pageSize) {

		Article a = new Article();
		HttpSession session = request.getSession(false);
		if (session == null) {
			return "redirect:/passport/login";// session.可能过期
		}
		User user = (User) session.getAttribute("user");

		a.setUserId(user.getId());

		PageInfo<Article> info = articleService.selects(a, page, pageSize);
		model.addAttribute("info", info);
		return "my/article/articles";
	}

	/**
	 * 
	 * @Title: publish
	 * @Description: 去 增加文章/发布文章
	 * @return
	 * @return: String
	 */
	@GetMapping("publish")
	public String publish() {

		return "my/article/publish";

	}

	/**
	 * 
	 * @Title: publish
	 * @Description: 去 发布图片集
	 * @return
	 * @return: String
	 */
	@GetMapping("publishpic")
	public String publishpic() {
		return "my/article/publishpic";
	}

	/**
	 * 
	 * @Title: publish
	 * @Description: 增加文章/发布文章
	 * @param article
	 * @return
	 * @return: boolean
	 */
	@ResponseBody
	@PostMapping("publish")
	public Result publish(HttpServletRequest request, ArticleWithBLOBs article,
			MultipartFile file) {

		if (!file.isEmpty()) {
			// 文件上传路径.把文件放入项目的 /resource/pic 下
			String path = request.getSession().getServletContext()
					.getRealPath("/resource/pic/");
			// 为了防止文件重名.使用UUID 的方式重命名上传的文件
			String oldFilename = file.getOriginalFilename();
			// a.jpg
			String newFilename = UUID.randomUUID()
					+ oldFilename.substring(oldFilename.lastIndexOf("."));
			File f = new File(path, newFilename);
			// 写入硬盘
			try {
				file.transferTo(f);
				article.setPicture(newFilename);// 标题图片
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		// 初始化设置
		article.setStatus(0);// 待审核
		HttpSession session = request.getSession(false);
		if (session == null) {
			return ResultUtil.error(1, "登录过期");
		}
		User user = (User) session.getAttribute("user");
		article.setUserId(user.getId());// 发布人
		article.setHits(0);
		article.setHot(0);
		article.setDeleted(0);
		article.setContentType(ArticleEnum.HTML.getCode());
		article.setCreated(new Date());
		article.setUpdated(new Date());

		return ResultUtil.success(articleService.insertSelective(article));

	}

	/**
	 * 
	 * @Title: publishpic
	 * @Description:  发布图片集
	 * @param article
	 * @return
	 * @return: boolean
	 */
	@ResponseBody
	@PostMapping("publishpic")
	public Result publishpic(HttpServletRequest request, ArticleWithBLOBs article,
			MultipartFile[] files, String[] descr) {
		String newFilename = null;
		List<ArticleVO> list = new ArrayList<ArticleVO>();// 用来存放图片的地址和描述
		int i = 0;
		for (MultipartFile file : files) {
			ArticleVO vo = new ArticleVO();
			if (!file.isEmpty()) {
				// 文件上传路径.把文件放入项目的 /resource/pic 下
				String path = request.getSession().getServletContext()
						.getRealPath("/resource/pic/");
				// 为了防止文件重名.使用UUID 的方式重命名上传的文件
				String oldFilename = file.getOriginalFilename();
				// a.jpg
				newFilename = UUID.randomUUID()
						+ oldFilename.substring(oldFilename.lastIndexOf("."));
				File f = new File(path, newFilename);
				vo.setUrl(newFilename);
				vo.setDescr(descr[i]);
				i++;
				list.add(vo);
				// 写入硬盘
				try {
					file.transferTo(f);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		article.setPicture(newFilename);// 标题图片
//		Gson gson = new Gson();
//		// 使用gson,把java对象转为json
//		article.setContent(gson.toJson(list));
		article.setContent(JSON.toJSONString(list));
		// 初始化设置
		article.setStatus(0);// 待审核
		HttpSession session = request.getSession(false);
		if (session == null) {
			return ResultUtil.error(1, "登录过期,请重新登录");
		}
		User user = (User) session.getAttribute("user");
		article.setUserId(user.getId());// 发布人
		article.setHits(0);
		article.setHot(0);
		article.setDeleted(0);
		article.setCreated(new Date());
		article.setUpdated(new Date());
		// 图片集标识
		article.setContentType(ArticleEnum.IMAGE.getCode());
		return ResultUtil.success(articleService.insertSelective(article));
	}

	/**
	 * @Title: comment 
	 * @Description: 添加评论
	 * @param articleId
	 * @param content
	 * @param request
	 * @return
	 * @return: Result<Comment>
	 */
	@PostMapping("comment")
	@ResponseBody
	public Result<Comment> comment(Integer articleId, String content,
			HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");
		ArticleWithBLOBs article = new ArticleWithBLOBs();
		article.setId(articleId);
		Comment comment = new Comment();
		comment.setUser(user);
		comment.setArticle(article);
		comment.setContent(content);
		comment.setCreated(new Date());
		commentService.insert(comment);
		return ResultUtil.success();
	}
	
	/**
	 * @Title: comments 
	 * @Description: 加载我的评论
	 * @param model
	 * @param pageNum
	 * @param pageSize
	 * @param request
	 * @return
	 * @return: String
	 */
	@RequestMapping("comments")
	public String comments(Model model, @RequestParam(defaultValue = "1") Integer pageNum,
			@RequestParam(defaultValue = "3") Integer pageSize,
			HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		User user = (User) session.getAttribute("user");
		PageInfo<Comment> page = commentService.selectbyId(user, pageNum, pageSize);
		model.addAttribute("info", page);
		return "my/comment/comments";
	}

	/**
	 * @Title: deleteComment 
	 * @Description: 删除评论
	 * @param id
	 * @return
	 * @return: Result<Collect>
	 */
	@PostMapping("deleteComment")
	@ResponseBody
	public Result<Collect> deleteComment(Integer id) {
		commentService.deleteComment(id);
		return ResultUtil.success();
	}

}
