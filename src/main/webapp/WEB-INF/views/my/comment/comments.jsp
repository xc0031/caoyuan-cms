<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>我的评论</title>
</head>
<body class="container">
	<h2>我的评论</h2>
	<hr>
	<c:forEach items="${info.list}" var="c">
		<dl>
			<dt><h3><a href="/article?id=${c.article.id}" target="_blank">${c.article.title}</a></h3></dt>
			<dd><fmt:formatDate value="${c.created}" pattern="yyyy-MM-dd HH:mm:ss"/> &nbsp; <button class="btn btn-info btn-sm" onclick="deleteComment(${c.id})">删除</button></dd>
			<br>
			<dt><h4>${c.content }</h4></dt>
    <hr>
		</dl>
	</c:forEach>
    <jsp:include page="/WEB-INF/views/common/pages.jsp"></jsp:include>
	<script type="text/javascript">
		function goPage(pageNum){
			$("#center").load("/my/comments?pageNum="+pageNum);
		}
		function deleteComment(id){
			if(confirm("确认要删除?")){
				$.post(
					"/my/deleteComment?id="+id,
					function(result){
						alert(result.msg);
						if(result.code==0){
							$("#center").load("/my/comments");
						}
					}
				)
			}
		}
	</script>
</body>
</html>