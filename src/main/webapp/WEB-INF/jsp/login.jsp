<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="en">

<head>

<link rel="stylesheet"href="<%=request.getContextPath()%>/css/style.css" />
<title>Login</title>

</head>

<body>
	
	<div class="titlePadding">
		
		<div class="title titleContainer">
		
			<img src="<%=request.getContextPath()%>/images/Logo.svg" alt="Logo" />
		
			<h1>Bank Of Ben</h1>
		
		</div>
	
	</div>

	<div class="navPadding loginNavBarContainer">
	
		<a href="/BankApp/"><button class="button-1">Home</button></a>
	
	</div>

	<div class="loginContainer">
	
		<div class="loginImageContainer">
	
			<img src="<%=request.getContextPath()%>/images/Login.svg" alt="Login" />
	
		</div>

		<div class="loginFormPadding loginFormContainer">
		
			<div>
		
				<h2>Login</h2><br />
			
			</div>

			<form action="login" method="post">

				<div class="innerLoginFormContainer">

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
				
				</div>
			
			</form>
	
		</div>
	
	</div>

</body>

</html>
