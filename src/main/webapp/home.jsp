<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"href="<%=request.getContextPath()%>/css/style.css">
<title>Home</title>

</head>

<body>

	<jsp:include page="/WEB-INF/jsp/header.jsp" />

	<div class="buttonContainer">
		
		<a href="<%=request.getContextPath()%>/app/login"><button class="button-2">LOGIN</button></a>
		
	</div>

	<div class="imageContainer">
		
		<img src="<%=request.getContextPath()%>/images/Home.svg" alt="Home">
	
	</div>

	<div class="detailPadding">

		<p class="font1 titleContainer">TRUST AND TRANSPARENCY</p>
	
	</div>

	<div class="detailPadding">
		
		<div class="container">
		
			<p class="font1">Phone : +91-9791289041</p>
		
			<p class="font1">Mail : bob@gmail.com</p>
		
		</div>
	
	</div>

</body>

</html>