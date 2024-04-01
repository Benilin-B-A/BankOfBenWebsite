<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">

<title>ChangeCredential</title>
<link rel="stylesheet"href="<%=request.getContextPath()%>/css/style.css" />

</head>

<body>

	<jsp:include page="header.jsp" />
	
	<div class="buttonContainer">
		
		<a href=""><button class="button-2">Back</button></a>
		
	</div>
	
	<div class="columnBodyContainer">

		<div class="transactionContainer loginFormPadding">
		
				<img src="<%=request.getContextPath()%>/images/changePassword.svg" alt="Login" />		
				
					
				<div class="innerLoginFormContainer">
				
					<%if (request.getAttribute("credentialType").equals("password")) {%>
			
					<h2>Change Password</h2>
			
				<form action="<%=request.getContextPath()%>/app/changePassword" method="post">
			
					<div>
						<input placeholder="Existing password" type="password" name="oldPassword" />
						<br> <br>
					</div>

					<div>
						<input placeholder="New password" type="password" name="newPassword" /> <br>
						<br>
					</div>

					<div>
						<button type="submit" class="button-2">CONFIRM</button>
					</div>

				</form>
				
				
			
				<div>
			
			</div>
		
		<%}else if (request.getAttribute("credentialType").equals("pin")) {%>
			
					<h2>Change Password</h2>
			
				<form action="<%=request.getContextPath()%>/app/changePin" method="post">
			
					<div>
						<input placeholder="Existing T-PIN" type="password" name="oldPin" />
						<br> <br>
					</div>

					<div>
						<input placeholder="New T-PIN" type="password" name="newPin" /> <br>
						<br>
					</div>

					<div>
						<button type="submit" class="button-2">CONFIRM</button>
					</div>

				</form>
				
				
			
				<div>
			
			</div>
		
		<%} %>
		</div>
		
	</div>
			
			
	</div>
		
</body>

</html>