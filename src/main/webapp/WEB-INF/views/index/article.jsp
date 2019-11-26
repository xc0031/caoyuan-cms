<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>文章详情</title>
<script type="text/javascript" src="/resource/js/jquery-3.2.1.js"></script>
<script type="text/javascript" src="/resource/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" type="text/css" href="/resource/css/bootstrap.min.css" />
<script type="text/javascript">
	//回到底部
	$(function() {
		if ('${info.pageNum}' > 1) {
			//刷新页面时自动到底部
			var h = $(document).height() - $(window).height();
			$(document).scrollTop(h);
		}
	})
	function collect() {
		var text = '${article.title}';//收藏的标题
		var url = window.location.href;//收藏的地址

		$.post("/collect", {
			text : text,
			url : url
		}, function(result) {
			alert(result.msg);
			if (result.code == 0) {
				$("#mc").html("<span style='font-size: 20px;color: red'>★(已收藏)</span>")
			}
		})
	}
	//增加评论
	function addComment() {
		var username = '${sessionScope.user.username}';
		if (username == '') {
			alert("请先登录");
			return;
		}
		if ($("[name = content]").val() == '') {
			$("#sp").text("评论内容不能为空");
			return;
		}
		var articleId = '${article.id}';
		var content = $("[name=content]").val();
		$.post("/my/comment", {
			articleId : articleId,
			content : content
		}, function(result) {
			alert(result.msg);
			if (result.code == 0) {
				location = "/article?id=${article.id}";
			}
		})
	}
	function goPage(pageNum){
		location = "/article?id=${article.id}&pageNum="+pageNum;
	}
</script>
</head>
<body>
	<div style="text-align: center; width: 80%; margin: 0 auto;">
		<dl>
			<dt>
				<h2>${article.title }</h2>
			</dt>
			<hr>
			<dd id="mc">
				<c:if test="${isCollect==1}">
					<span style="font-size: 20px; color: red">★ (已收藏)</span>
				</c:if>
				<c:if test="${isCollect!=1}">
					<span style="font-size: 20px; color: blue;">
						<a href="javascript:collect()">☆ (未收藏)</a>
					</span>
				</c:if>
			</dd>
			<dd>
				<fmt:formatDate value="${aritcle.updated }" pattern="yyyy-MM-dd HH:mm:ss" />
			</dd>
			<dd>${article.content }</dd>
		</dl>
	</div>
	<div class="container">
		<h3>发表评论</h3>
		<textarea rows="4" cols="" name="content" class="container-fluid"></textarea>
		<button type="button" class="btn btn-success btn-sm" onclick="addComment()">发表</button>
		<span id="sp"></span>
		<br>
		<br>
		<h4>最新评论</h4>
		<hr>
		<c:forEach items="${info.list }" var="c">
			<dl>
				<dt>
					<h5>${c.user.username}&emsp;<fmt:formatDate value="${c.created}" pattern="yyyy-MM-dd HH:mm:ss" />
					</h5>
				</dt>
				<dd>${c.content }</dd>
				<br>
				<hr>
			</dl>
		</c:forEach>
		<jsp:include page="/WEB-INF/views/common/pages.jsp"></jsp:include>
	</div>
</body>
</html>