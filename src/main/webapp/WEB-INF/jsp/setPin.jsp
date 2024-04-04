<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>
	
	<meta charset="UTF-8">

	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css" />

	<title>Insert title here</title>

</head>

<body>

	<jsp:include page="popUpScript.jsp" />
	
	<jsp:include page="header.jsp" />
	
	<div class="buttonContainer">
		
		<a href="<%=request.getContextPath()%>/app/accounts"><button class="button-2">Cancel</button></a>
		
	</div>

	<div>
	
		<br> <br>

		<div class="columnBodyContainer">

		<div class="transactionContainer loginFormPadding">
		
			<img src="<%=request.getContextPath()%>/images/SetPin.svg" alt="setPin" />		
				
			<div>
					
				<h2>SET T-PIN</h2>
				
				<p class="font3">You're one step away from making your first transaction</p>
				
				<form action="<%=request.getContextPath()%>/app/setPin" method="post">

					<div>
						<input placeholder="New T-PIN" type="password" name="newPin" required
								min=1 step=1 max=9999/> 
						<br><br>
					</div>
					
					<div>
						<input placeholder="Re-Enter New T-PIN"  type="password" name="reNewPin" required
								min=1 step=1 max=9999/> 
						<br>
					</div>
					
					<p class="font4">
						Pin must be a 4 digit number
					</p>
					
					<br>

					<button type="submit" class="button-2">CONFIRM</button>
					
					<br><br>
				
				</form>
			
			</div>
		
		</div>
		
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