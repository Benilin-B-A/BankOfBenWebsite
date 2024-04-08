<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>

	<meta charset="UTF-8">

	<title>ChangeCredential</title>

	<link rel="stylesheet"href="<%=request.getContextPath()%>/css/style.css" />

</head>

<body>

	<jsp:include page="popUpScript.jsp" />

	<jsp:include page="header.jsp" />
	
	<div class="buttonContainer">
		
		<a href="<%=request.getContextPath()%>/app/profile"><button class="button-2">Back</button></a>
		
	</div>
	
	<br><br>
	
	<div class="columnBodyContainer">

		<div class="transactionContainer loginFormPadding">
		
			<img src="<%=request.getContextPath()%>/images/changePassword.svg" alt="Login" />		
				
			<%if (request.getAttribute("credentialType").equals("password")) {%>
			
			<div>
					
				<h2>Change Password</h2>
				
				<form action="<%=request.getContextPath()%>/app/changePassword" method="post">
				
					<div>
						<input placeholder="Existing Password" type="password" name="oldPassword" required/>
						<br> <br>
					</div>

					<div>
						<input placeholder="New Password" type="password" name="newPassword" required
								pattern="^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,}$"/> 
						<br><br>
					</div>
					
					<div>
						<input placeholder="Re-Enter New Password"  type="password" name="reNewPassword" required
								pattern="^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,}$"/> 
						<br>
					</div>
					
					<p class="font4">
						Password must contain <br>*Atleast 8 characters <br> *One uppercase <br>
						*One number<br> *One special character
					</p>
					
					<br>

					<div class="profileButtonContainer">
						<button type="submit" class="button-2">CONFIRM</button>
					</div>
					
					<br><br>
				
				</form>
			
			</div>
		
			<%}else if (request.getAttribute("credentialType").equals("pin")) {%>
			
			<div>
					
				<h2>Change T-PIN</h2>
				
				<form action="<%=request.getContextPath()%>/app/changePIN" method="post">
				
					<div>
						<input placeholder="Existing T-Pin" type="password" name="oldPin" required
								min=0 max=9999 step=1/>
						<br> <br>
					</div>

					<div>
						<input placeholder="New T-Pin" type="password" name="newPin" required
								min=0 max=9999 step=1/> 
						<br><br>
					</div>
					
					<div>
						<input placeholder="Re-Enter New Pin"  type="password" name="reNewPin" required
								min=0 max=9999 step=1/> 
						<br><br>
					</div>
					
					<p class="font4">
						Pin must be a 4 digit number
					</p>

					<div>
						<button type="submit" class="button-2">CONFIRM</button>
					</div>
				
				</form>
			
			</div>
			
			<%} %>
			
		</div>
		
	</div>
	
	<%
	String message = (String) request.getAttribute("errorMessage");
	if( message != null) {%>
								
		<div class="messageContainer">
			<p class="errorMessage" id="msg"><%=message%></p>
		</div>
						
	<%}	%>
		
</body>

</html>