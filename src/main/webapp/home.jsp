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

	<div class="bContainer">
	
	<jsp:include page="/WEB-INF/jsp/header.jsp" />

	<div class="buttonContainer">
		
		<a href="<%=request.getContextPath()%>/app/login"><button class="button-2">LOGIN</button></a>
		
	</div>
	
	<br><br>
	
	<div class="container">
	
		<p CLASS="font2">- - - WELCOME TO BANK OF BEN NET-BANKING PORTAL - - -</p>
	
		<p class="font2">- - EXCELLING THROUGH TRUST AND TRANSPARENCY - -</p>
	
	</div>

	<br><br><br>

	<div class="columnBodyContainer">
		
		<img src="<%=request.getContextPath()%>/images/Home.svg" alt="Home">
		
		<br><br><br>
		
		<div class="container detailPadding">
		
			<p class="font1">Phone : +91-9791289041</p>
		
			<p class="font1">E-Mail : bankofben@gmail.com</p>
		
		</div>
	
	</div>
	
	</div>
		
</body>

</html>