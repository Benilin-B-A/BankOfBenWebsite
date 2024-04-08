<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="en">

<head>

	<link rel="stylesheet"href="<%=request.getContextPath()%>/css/style.css" />

	<title>Login</title>

</head>

<body>

	<jsp:include page="popUpScript.jsp" />
	
	<jsp:include page="header.jsp" />
	
	<div class="buttonContainer">
		
		<a href="/BankApp">
			<button class="button-2">Home</button>
		</a>
	
	</div>
	
	<br><br><br><br><br>
	
	<div class="columnBodyContainer">

		<div class="transactionContainer loginFormPadding">
		
			<img class="img" src="<%=request.getContextPath()%>/images/Login.svg" alt="Login" />
			
			<div>
			
			<h2>Login</h2> 
					
			<form action="<%=request.getContextPath()%>/app/login" method="post">
			
				<div class="innerLoginFormContainer">
				
					<br>
					
					<input placeholder="User ID" type="text" pattern="^[0-9]+$" name="userId" min=1
					 step=1 required title="Enter valid User ID"/><br /><br />
					
					<input placeholder="Password" type="password" name="password" required/><br /><br />
					
					<button type="submit" class="button-4">Submit</button>
						
					<br><br>
					
				</div>
			
			</form>
			
			</div>
			
		</div>
			
	</div>
	
	<%
	String errorMessage = (String) request.getAttribute("errorMessage");
	if ( errorMessage != null) {
	%>
		<div class="messageContainer">
		
			<p id="msg" class="errorMessage">
				<%=errorMessage%>
			</p>
		
		</div>
	<%
	}
	String successMessage = (String) request.getAttribute("successMessage");
	if ( successMessage != null) {
	%>
		<div class="messageContainer">
		
			<p id="msg" class="successMessage">
				<%=successMessage%>
			</p>
		
		</div>
	<%
	}
	%>
	
</body>

</html>
