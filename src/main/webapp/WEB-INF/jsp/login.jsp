<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="en">

<head>

<link rel="stylesheet"href="<%=request.getContextPath()%>/css/style.css" />
<title>Login</title>

</head>

<body>
	
	<jsp:include page="header.jsp" />
	
	<div class="buttonContainer">
		
		<a href="/BankApp"><button class="button-2">Home</button></a>
		
	</div>
	
	<br><br><br><br><br>
	
	<div class="columnBodyContainer">

		<div class="transactionContainer loginFormPadding">
		
					<img src="<%=request.getContextPath()%>/images/Login.svg" alt="Login" />
		
		
				<form action="<%=request.getContextPath()%>/app/login" method="post">
					
				<div class="innerLoginFormContainer">
				
					<h2>Login</h2> <br><br>

					<div>
						
						<input placeholder="User ID" type="number" name="userId" /><br /><br /> <br />
					
					</div>
					
					<div>
					
						<input placeholder="Password" type="password" name="password" /><br /><br /> <br />
					
					</div>
					
					<div>
						
						<%
						if (request.getAttribute("errorMessage") != null) {
						%>
						
						<p id="errorMessage" class="errorMessage">
							<%=request.getAttribute("errorMessage")%>
						</p>
						
						<%
						}
						%><br />
						
					</div>
					
					<div>
					
						<button type="submit" class="button-2">Submit</button>
					
					</div>
					<br><br>
				</div>
			
			</form>
			
		</div>
			
			
	</div>
	

</body>

</html>
