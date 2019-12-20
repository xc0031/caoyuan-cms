package com.caoyuan.cms.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

/**
 * @ClassName: ESUtils
 * @Description: 
 * @author: chj
 * @date: 2019年7月24日 上午10:14:13
 */
public class ESUtils {

	private static Logger logger = Logger.getLogger(ESUtils.class);

	/**
	 * 保存及更新方法
	 * @param elasticsearchTemplate
	 * @param id
	 * @param object
	 */
	public static void saveObject(ElasticsearchTemplate elasticsearchTemplate, String id,
			Object object) {
		// 创建所以对象
		IndexQuery query = new IndexQueryBuilder().withId(id).withObject(object).build();
		// 建立索引
		elasticsearchTemplate.index(query);
	}

	/**
	 * 批量删除
	 * @param elasticsearchTemplate
	 * @param clazz
	 * @param ids
	 */
	public static void deleteObject(ElasticsearchTemplate elasticsearchTemplate,
			Class<?> clazz, Integer ids[]) {
		for (Integer id : ids) {
			// 建立索引
			elasticsearchTemplate.delete(clazz, id + "");
		}
	}

	/**
	 * @param <T>
	 * @Title: selectById
	 * @Description: 根据id在es服务启中查询对象
	 * @param elasticsearchTemplate
	 * @param clazz
	 * @param id
	 * @return
	 * @return: Object
	 */
	public static <T> T selectById(ElasticsearchTemplate elasticsearchTemplate,
			Class<T> clazz, Integer id) {
		GetQuery query = new GetQuery();
		query.setId(id + "");
		return elasticsearchTemplate.queryForObject(query, clazz);
	}

	// 查询操作

	/**
	 * @param elasticsearchTemplate 模板对象
	 * @param clazz	实体类的class对象
	 * @param classes 实体类中实体类型的成员变量的类的class集合
	 * @param page	当前页，从1开始
	 * @param pageSize	每页的条数
	 * @param sortField	根据这个字段进行排序
	 * @param fieldNames	要搜索的字段名
	 * @param value	具体要搜索的数据
	 * @return
	 */
	public static <T> AggregatedPage<T> selectObjects(
			ElasticsearchTemplate elasticsearchTemplate, Class<T> clazz,
			List<Class> classes, Integer page, Integer pageSize, String sortField,
			String fieldNames[], String value) {
		page = page - 1;
		AggregatedPage<T> pageInfo = null;
		logger.info("采用es进行数据库的查询操作开始！！！！！！！！！！！！！！！！！！！！！！！！");
		// 创建Pageable对象
		Pageable pageable = PageRequest.of(page, pageSize,
				Sort.by(Sort.Direction.ASC, sortField));
		// 查询对象
		SearchQuery query = null;
		// 查询条件高亮的构建对象
		QueryBuilder queryBuilder = null;
		if (value != null && !"".equals(value)) {
			// 高亮拼接的前缀与后缀
			String preTags = "<font color=\"red\">";
			String postTags = "</font>";
			// 定义创建高亮的构建集合对象
			HighlightBuilder.Field highlightFields[] = new HighlightBuilder.Field[fieldNames.length];
			for (int i = 0; i < fieldNames.length; i++) {
				// 这个代码有问题
				highlightFields[i] = new HighlightBuilder.Field(fieldNames[i])
						.preTags(preTags).postTags(postTags);
			}
			// 创建queryBuilder对象
			queryBuilder = QueryBuilders.multiMatchQuery(value, fieldNames);
			query = new NativeSearchQueryBuilder().withQuery(queryBuilder)
					.withHighlightFields(highlightFields).withPageable(pageable).build();
			pageInfo = elasticsearchTemplate.queryForPage(query, clazz,
					new SearchResultMapper() {
						@Override
						public <T> AggregatedPage<T> mapResults(SearchResponse response,
								Class<T> clazz, Pageable pageable) {
							List<T> content = new ArrayList<T>();
							long total = 0l;
							try {
								// 查询结果
								SearchHits hits = response.getHits();
								if (hits != null) {
									// 获取总记录数
									total = hits.getTotalHits();
									// 获取结果数组
									SearchHit[] searchHits = hits.getHits();
									// 判断结果
									if (searchHits != null && searchHits.length > 0) {
										// 遍历结果
										for (int i = 0; i < searchHits.length; i++) {
											// 对象值
											T entity = clazz.newInstance();
											// 获取具体的结果
											SearchHit searchHit = searchHits[i];
											// 获取对象的所有的字段
											Field[] fields = clazz.getDeclaredFields();
											// 遍历字段对象
											for (int k = 0; k < fields.length; k++) {
												// 获取字段对象
												Field field = fields[k];
												// 暴力反射
												field.setAccessible(true);
												// 字段名称
												String fieldName = field.getName();
												if (!fieldName
														.equals("serialVersionUID")) {
													HighlightField highlightField = searchHit
															.getHighlightFields()
															.get(fieldName);
													if (highlightField != null) {
														// 高亮 处理 拿到 被<font color='red'>
														// </font>结束所包围的内容部分
														String value = highlightField
																.getFragments()[0]
																		.toString();
														// 注意一下他是否是 string类型
														field.set(entity, value);
													} else {
														// 获取某个字段对应的 value值
														Object value = searchHit
																.getSourceAsMap()
																.get(fieldName);
														// System.out.println(value);
														// 获取字段的类型
														Class<?> type = field.getType();
														if (type == Date.class) {
															if (value != null) {
																// 如果不为空，则转换成Date类型
																Date value_date = null;
																if (value
																		.getClass() == Long.class) {
																	// 如果是Long类型
																	value_date = new Date(
																			Long.valueOf(
																					value + ""));
																} else {
																	// 如果是String类型
																	SimpleDateFormat sdf = new SimpleDateFormat(
																			"yyyy-MM-dd");
																	value_date = sdf
																			.parse(value
																					.toString());
																}
																// bug
																field.set(entity,
																		value_date);
															}
														} else if (type.isEnum()) {
															if (value != null) {
																// 枚举
																field.set(entity,
																		Enum.valueOf(
																				(Class<Enum>) type,
																				value.toString()));
															}

														} else if (classes != null
																&& classes
																		.contains(type)) {
															if (value != null) {
																// 将实体类对象实例化
																Object obj = getEntityObject(
																		value, type,
																		classes);

																// 将对象赋值
																field.set(entity, obj);
															}
														} else {
															field.set(entity, value);
														}
													}
												}
											}
											content.add(entity);
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							return new AggregatedPageImpl<>(content, pageable, total);
						}

						// 递归方法，生成实体类对象
						private Object getEntityObject(Object value, Class<?> type,
								List<Class> classes) throws InstantiationException,
								IllegalAccessException, ParseException {
							// 实体类
							Object obj = type.newInstance();
							Map map = (HashMap) value;

							// 获取所有字段
							Field[] fields2 = type.getDeclaredFields();
							for (Field field2 : fields2) {

								// 排除静态变量和常量
								int mod = field2.getModifiers();
								if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
									continue;
								}

								// 暴力反射
								field2.setAccessible(true);

								// 从map中获取对应的字段的值
								Object value2 = map.get(field2.getName());

								// 处理日期Date类型
								Class<?> type2 = field2.getType();
								if (type2 == Date.class) {
									if (value2 != null) {
										// 如果不为空，则转换成Date类型

										Date value2_date = null;
										if (value2.getClass() == Long.class) {
											// 如果是Long类型
											value2_date = new Date(
													Long.valueOf(value2 + ""));

										} else {
											// 如果是String类型
											SimpleDateFormat sdf = new SimpleDateFormat(
													"yyyy-MM-dd");

											value2_date = sdf.parse(value2.toString());
										}

										// bug
										field2.set(obj, value2_date);
									}
								} else if (type2.isEnum()) {
									if (value2 != null) {
										// 枚举
										field2.set(obj, Enum.valueOf((Class<Enum>) type2,
												value2.toString()));
									}
								} else if (classes != null && classes.contains(type2)) {
									if (value2 != null) {

										// 将实体类对象实例化
										Object obj2 = getEntityObject(value2, type2,
												classes);

										// 将对象赋值
										field2.set(obj, obj2);
									}
								} else {

									field2.set(obj, value2);
								}

							}
							return obj;
						}
					});

		} else {
			// 没有查询条件的的时候，获取es中的全部数据 分页获取
			query = new NativeSearchQueryBuilder().withPageable(pageable).build();
			pageInfo = elasticsearchTemplate.queryForPage(query, clazz);
		}
		return pageInfo;
	}

}
