<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css" />
<title>Insert title here</title>
</head>
<body>
	<jsp:include page="header.jsp" />

	<div>
		<h3>SET TPIN</h3>
		<br> <br>

		<div>
			<p>Set T-PIN to complete your first transaction</p>
			<form action="setTPIN" method="post">
				<div>
					<input placeholder="NEW T-PIN" type="password" name="tPIN" />
					<br> <br>
				</div>

				<div>
					<input placeholder="CONFIRM NEW T-PIN" type="password" name="confTPIN" /> <br>
					<br>
				</div>

				<div>
					<button type="submit">CONFIRM</button>
				</div>

			</form>
			
			<%String errorMessage = (String) request.getAttribute("error");
			if(errorMessage!=null){%>
			<p><%=errorMessage%></p>
			<%} %>

		</div>
	</div>
</body>
</html>