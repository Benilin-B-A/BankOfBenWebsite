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

		<p class="font1">BankOfBen, established in 2024, has swiftly
			emerged as a cornerstone in the financial sector, prioritizing
			customer-centric services and innovative solutions. With a commitment
			to excellence, it offers a comprehensive range of banking products
			tailored to meet diverse financial needs. Transparency and integrity
			are at the core of BankOfBen's operations
		</p>

		<div class="container">
			
			<button class="button-1">Loans</button>
			
			<br> <br>
			
			<button class="button-1">Investments</button>
			
			<br> <br>
			
			<button class="button-1">Other Services</button>
			
			<br> <br>
		
		</div>
	
	</div>

	<div class="detailPadding">
		
		<div class="container">
		
			<p class="font1">Phone : +91-9791289041</p>
		
			<p class="font1">Mail : bob@gmail.com</p>
		
		</div>
	
	</div>

</body>

</html>