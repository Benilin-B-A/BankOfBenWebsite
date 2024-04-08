<%@page import="com.bank.services.EmployeeServices"%>
<%@page import="java.util.List"%>
<%@page import="org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression"%>
<%@page import="com.bank.services.CustomerServices"%>
<%@page import="com.bank.pojo.Customer"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>

	<title>MoneyTransfer</title>

	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">

</head>

<body>

	<jsp:include page="popUpScript.jsp" />

	<jsp:include page="header.jsp" />
	
	<%Object userObj = request.getSession().getAttribute("user");
	if (userObj instanceof CustomerServices){%>
	
		<jsp:include page="customerNav.jsp" />
	
	<%}else if(userObj instanceof EmployeeServices){ %>
	
	<jsp:include page="employeeNav.jsp" />

<%}else{%>
	<jsp:include page="adminNav.jsp" />

<%} %>
	
	<br><br><br><br>

	<div class="columnBodyContainer loginFormPadding">

		<div class="profileButtonContainer">
		
			<%if(!(userObj instanceof CustomerServices)){ %>
			<form action="<%=request.getContextPath()%>/app/transaction" method="get">
				<input type="hidden" name="transactionType" value="withdraw">
				<button class="button-2">Withdraw</button>
			</form>
				
			<form action="<%=request.getContextPath()%>/app/transaction" method="get">
				<input type="hidden" name="transactionType" value="deposit">
				<button class="button-2">Deposit</button>
			</form>
				
			<%} %>
				
			<form action="<%=request.getContextPath()%>/app/transaction" method="get">
				<input type="hidden" name="transactionType" value="withinBank">
				<button class="button-2">Transfer Within Bank</button>
			</form>
			
			<form action="<%=request.getContextPath()%>/app/transaction" method="get">
				<input type="hidden" name="transactionType" value="toOtherBank">
				<button class="button-2">Transfer To Other Bank</button>
			</form>
				
		</div>
		
		<br> <br>
			
		<%String type = (String) request.getAttribute("type");
		if(type!=null && type.equals("withdraw")){%>
			
		<div class="transactionContainer">
				
			<img src="<%=request.getContextPath()%>/images/Withdraw.svg" alt="withdraw">
			
			<form action="withdraw" method="post">
				
			<div class="innerLoginFormContainer">

				<div>
			
					<input placeholder="Account Number" type="number" name="accNumber" required
							min=1 step=1/>
					<br> <br>
			
				</div>
				

				<div>
			
					<input placeholder="Amount" type="number" name="amount" required
							min=1 step=1/> <br>
					<br>
			
				</div>
				
				<div>
					
					<button type="submit" class="button-2">Withdraw</button>
					
				</div>
				
			</div>
			
			</form>
			
		</div>
			
		<%}else if(type!=null && type.equals("deposit")){ %>
			
		<div class="transactionContainer">
				
			<img src="<%=request.getContextPath()%>/images/Deposit.svg" alt="deposit">
			
			<form action="deposit" method="post">
				
			<div class="innerLoginFormContainer">

				<div>
					<input placeholder="Account Number" type="number" name="accNumber" required
							min=1 step=1/>
					<br> <br>
				</div>
				

				<div>
					<input placeholder="Amount" type="number" name="amount" required
							min=1 step=1/> <br>
					<br>
				</div>
				
				<div>
					<button type="submit" class="button-2">Deposit</button>
				</div>
				
			</div>
			
			</form>
			
		</div>
			
		<%}else{ %>
			
		<div class="transactionContainer">

			<form action="sendMoney" method="post">
				
				<div class="innerLoginFormContainer">
				
					<%if(!(userObj instanceof CustomerServices)){ %>
					<div>
		
						<input placeholder="Account Number" type="number" name="senderAccNum" required
							min=1 step=1/> <br>
						<br>
		
					</div>
				
					<%}
					if((userObj instanceof CustomerServices)){ 
					%>
					
					<div>
					
					<label for="accDropDown" class="font3">Account Number : </label>
					
					<select id="accDropDown" name="ownAccNumber">
									
						<%List<Long> list = (List<Long>) request.getAttribute("accList");
							for(int i=0;i<list.size();i++){
								Long accNum = list.get(i);
							if(((CustomerServices) userObj).isPrimary(accNum)){%>
								
								<option value="<%=accNum%>" selected><%=accNum%></option>
							<%}else{ %>
							
  				 	 		<option value="<%=accNum%>"><%=accNum%></option>
    					
    					 <%} }%>
 					</select>
					
					</div>
					
					<br>
						
					<%}
					%>

					<div>
						
						<input placeholder="Recepient Account Number" type="number" name="accNumber" required
							min=1 step=1/>
						<br> <br>
		
					</div>

					<div>
				
						<input placeholder="Amount" type="number" name="amount" required
							min=1 step=1/> <br>
						<br>
				
					</div>
				
					<%
					if (type != null) {
						if (request.getAttribute("type").equals("outside")) {
					%>
				
					<div>
						<input placeholder="IFSC" type="text" name="iFSC" required/> <br> <br>
					</div>

					<input value="outside" type="hidden" name="type" />

					<%
					}
					} else {
					%>
					
					<input value="within" type="hidden" name="type" />
				
					<%
					}
					%>

					<div>
						<input placeholder="Description" type="text" name="description" required/>
						<br> <br>
					</div>
				
					<%if(userObj instanceof CustomerServices){ %>
					
					<div>
						<input placeholder="T-PIN" type="password" name="tpin" required
							min=1 max=9999 step=1/> <br>
						<br>
					</div>
					<%} %>
					
					<div>
					
						<button type="submit" class="button-2">Send</button>
					
					</div>
				
				</div>
			
			</form>
			
			<img src="<%=request.getContextPath()%>/images/Transaction.svg" alt="TransferMoney">
		
		</div>
			
		<%} %>
		
	</div>
	
	<%String msg = (String) request.getAttribute("successMessage");
		if( msg != null) {%>
				
	<div class="messageContainer">
		<p class="successMessage" id="msg"><%=msg%></p>
	</div>
	
	<%}
	String message = (String) request.getAttribute("errorMessage");
		if( message != null) {%>
				
	<div class="messageContainer">
		<p class="errorMessage" id="msg"><%=message%></p>
	</div>
	
	<%}	%>

</body>

</html>
