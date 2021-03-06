package com.caoyuan.cms.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.domain.Channel;
import com.caoyuan.cms.domain.Collect;
import com.caoyuan.cms.domain.Comment;
import com.caoyuan.cms.domain.Links;
import com.caoyuan.cms.domain.User;
import com.caoyuan.cms.service.ArticleService;
import com.caoyuan.cms.service.CategoryService;
import com.caoyuan.cms.service.ChannelService;
import com.caoyuan.cms.service.CollectService;
import com.caoyuan.cms.service.CommentService;
import com.caoyuan.cms.service.LinksService;
import com.caoyuan.cms.utils.ArticleEnum;
import com.caoyuan.cms.utils.Result;
import com.caoyuan.cms.utils.ResultUtil;
import com.caoyuan.cms.vo.ArticleVO;
import com.cy.util.StringUtil;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;

/**
 * @ClassName:   IndexController
 * @Description: 
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午6:59:05
 */
@Controller
public class IndexController {
	@Resource
	private ChannelService channelService;// 栏目

	@Resource
	private ArticleService articleService;// 文章

	@Resource
	private CategoryService categoryService;// 分类
	@Resource
	private LinksService linksService;// 分类
	@Resource
	private CollectService collectService;// 收藏

	@Resource
	private CommentService commentService;

	@Resource
	private ThreadPoolTaskExecutor executor;// 开启线程池

	@Resource
	private RedisTemplate<String, ?> redisTemplate;// 储存点击量的key(string类型)

	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;// kafka消息队列处理数据库点击量+1

	/**
	 * 
	 * @Title: index
	 * @Description: 首页
	 * @param article
	 * @param model
	 * @param page
	 * @param pageSize
	 * @return
	 * @return: String
	 */
	@RequestMapping(value = { "", "/", "index" })
	public String index(Article article, Model model,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "5") Integer pageSize, String key) {
		// 访问方法开始时间
		long s1 = System.currentTimeMillis();

		article.setStatus(1);// 显示审审核过的文章
		article.setDeleted(0);// 查询未删除的
		article.setContentType(ArticleEnum.HTML.getCode());
		Thread t1 = null;
		Thread t2 = null;
		Thread t3 = null;
		Thread t4 = null;
		Thread t5 = null;

		// 查询出左侧栏目
		t1 = new Thread(() -> {
			List<Channel> channels = channelService.selects();
			model.addAttribute("channels", channels);
		});

		// 热门文章
		t2 = new Thread(() -> {
			// 判断中间区域是高亮搜索还是热门
			if (StringUtil.hasText(key)) {
				// 如果搜索条件不为空，则查询es，进行高亮显示
				PageInfo<Article> info = articleService.selectEs(key, page, pageSize);
				model.addAttribute("info", info);
				model.addAttribute("key", key);
			} else {
				// 如果搜索条件为空，则显示热门文章
				// 如果栏目为空则默认显示热点
				if (article.getChannelId() == null) {
					// 查询热点文章的列表
					Article hot = new Article();
					hot.setStatus(1);// 审核过的
					hot.setHot(1);// 热点文章
					hot.setDeleted(0);// 未删除
					hot.setContentType(ArticleEnum.HTML.getCode());
					PageInfo<Article> info = articleService.selectHot(hot, page,
							pageSize);
					model.addAttribute("info", info);
				} else {
					// 分类文章和文章分类
					// 显示分类文章
					// 1查询出来栏目下分类
					List<Category> categorys = categoryService
							.selectsByChannelId(article.getChannelId());
					model.addAttribute("categorys", categorys);
					// 2.显示分类下的文章
					PageInfo<Article> info = articleService.selects(article, page,
							pageSize);
					model.addAttribute("info", info);
				}
			}
		});

		// 右侧边栏显示最新的5遍文章
		t3 = new Thread(() -> {
			// 右侧边栏显示最新的5遍文章
			Article lastArticle = new Article();
			lastArticle.setStatus(1);// 审核通过的
			lastArticle.setDeleted(0);
			lastArticle.setContentType(ArticleEnum.HTML.getCode());
			PageInfo<Article> lastInfo = articleService.selectLast(lastArticle, 1, 5);
			model.addAttribute("lastInfo", lastInfo);

		});
		// 查询出图片集
		t4 = new Thread(() -> {
			Article picArticle = new Article();
			picArticle.setStatus(1);// 审核通过的
			picArticle.setDeleted(0);
			picArticle.setContentType(ArticleEnum.IMAGE.getCode());
			PageInfo<Article> picInfo = articleService.selectPic(picArticle, 1, 5);
			model.addAttribute("picInfo", picInfo);
		});

		// 友情链接
		t5 = new Thread(() -> {
			PageInfo<Links> info = linksService.selects(1, 10);
			model.addAttribute("linksInfo", info);
		});

		// 封装查询条件
		model.addAttribute("article", article);
		// 开启线程
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();

		try {
			// 让子线程先运行.主线程最后运行.返回首页
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long s2 = System.currentTimeMillis();
		System.out.println("首页用时:=============================>" + (s2 - s1));

		return "index/index";
	}

	/**
	 * 
	 * @Title: article
	 * @Description: 文章详情
	 * @param model
	 * @return
	 * @return: String
	 */
	@RequestMapping("article")
	public String article(Model model, @RequestParam(defaultValue = "1") Integer pageNum,
			@RequestParam(defaultValue = "3") Integer pageSize, Integer id,
			HttpServletRequest request) {
		ArticleWithBLOBs article = articleService.selectByPrimaryKey(id);
		// A卷：为CMS系统文章最终页（详情页），每访问一次就同时往文章表的浏览量字段加1，
		// 如果一篇文章集中一时刻上百万次浏览，就会给数据库造成压力。现在请你利用Redis提高性能，
		// 当用户浏览文章时，将“Hits_${文章ID}_${用户IP地址}”为key，
		// 查询Redis里有没有该key，如果有key，则不做任何操作。如果没有，
		// 则使用Spring线程池异步执行数据库加1操作，并往Redis保存key为Hits_${文章ID}_${用户IP地址}，
		// value为空值的记录，而且有效时长为5分钟。

		// 获取远程请求对象的ip地址
		String ip = request.getRemoteAddr();
		String key = "Hits_" + id + "_" + ip;
		// 如果redis中没这个key,证明5分钟内这个用户是第一次点击
		if (!redisTemplate.hasKey(key)) {
			// 使用Spring线程池
			executor.execute(() -> {
				// 点击量+1,这里使用mysql中+1,能避免线程安全问题
				articleService.updateHits(id);
				// 在redis中添加key,5分钟内再点击,不计算点击量
				redisTemplate.opsForValue().set(key, null, 5, TimeUnit.MINUTES);
				System.out.println(id + "=====点击量修改成功");
			});
		}
		// B卷要求kafka修改点击量+1
		// kafkaTemplate.sendDefault("article_updateHits", id+"");

		// 评论
		PageInfo<Comment> commentInfo = commentService.selects(article.getId(), pageNum,
				pageSize);
		// 检查当前点击人是否登录.如果登录则根据标题和登录人查询是否收藏该文章
		HttpSession session = request.getSession(false);
		if (null != session) {
			User user = (User) session.getAttribute("user");
			// 判断是否收藏
			int i = collectService.selectByText(article.getTitle(), user);
			model.addAttribute("isCollect", i);
		}

		model.addAttribute("article", article);
		model.addAttribute("info", commentInfo);
		return "index/article";
	}

	/**
	 * 
	 * @Title: article
	 * @Description: 文章详情
	 * @param model
	 * @return
	 * @return: String
	 */
	@RequestMapping("articlepic")
	public String articlepic(Model model, Integer id) {

		ArticleWithBLOBs article = articleService.selectByPrimaryKey(id);
		String string = article.getContent();
		System.out.println(string);
		List<ArticleVO> list = JSON.parseArray(string, ArticleVO.class);
		model.addAttribute("title", article.getTitle());// 标题
		model.addAttribute("list", list);// 标题包含的 图片的地址和描述
		System.out.println(list);
		return "index/articlepic";
	}

	/**
	 * 
	 * @Title: collect 
	 * @Description: 收藏
	 * @param collect
	 * @return
	 * @return: Result<Collect>
	 */
	@ResponseBody
	@PostMapping("/collect")
	public Result<Collect> collect(Collect collect, HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		if (null == session) {
			return ResultUtil.error(1, "收藏失败,登录可能过期");
		}
		User user = (User) session.getAttribute("user");
		if (null == user) {
			return ResultUtil.error(1, "收藏失败,登录可能过期");
		}
		collect.setUser(user);
		collectService.insert(collect);
		return ResultUtil.success();
	}

}
