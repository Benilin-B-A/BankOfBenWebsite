<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="com.bank.enums.AccountType"%>
<%@page import="org.json.JSONObject" %>
<%@page import="com.bank.pojo.Account" %>
<%@page import="com.bank.enums.Status" %>
<%@page import="java.util.Iterator" %>

<!DOCTYPE html>

<html>

<head>

	<meta charset="UTF-8">

	<title>Accounts</title>

	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">

</head>

<body>

	<jsp:include page="popUpScript.jsp" />

	<jsp:include page="header.jsp" />
	
	<jsp:include page="customerNav.jsp" />
	
	<br><br><br>
	
	<div class="bContainer">
	
	<div class="bodyContainer infoPadding">
	
		<div class="innerContainer">
		
			<div class="accContainer individualAccPadding">
		
				<%
				JSONObject account=(JSONObject) request.getAttribute("account");
				%>
														
				<table class="table-format2">
															
					<tr>
						<td class="font2">Primary Account</td>
					</tr>
					
					<tr>
						<td class="font3">Account Number</td>
						<td class="font2"><%=account.get("accNum")%></td>
					</tr>
															
					<tr>
						<td class="font3">Balance</td>
						<td class="font2">Rs. <%=account.get("balance")%></td>
					</tr>
															
					<tr>
						<td class="font3">Branch</td>
						<td class="font2"><%=account.get("branch")%></td>
					</tr>
															
					<tr>
						<td class="font3">Account Type</td>
						<td class="font2">
							<%
							JSONObject typeObj = account.getJSONObject("type");
							int type = typeObj.getInt("type");
							if(type == 1){%>
								Current
								
							<%} else{%>
								Savings
							
							<%} %>
								
						</td>
					</tr>
					
					<tr>
						<td class="font3">Status</td>
						<td class="font2">
							<%
							JSONObject statusObj = account.getJSONObject("status");
							int status = statusObj.getInt("state");
							if(status == 1){%>
								Active
								
							<%} else if(status ==2) {%>
								Inactive
							
							<%} else{%>
								Blocked
							
							<%} %>
						</td>
					</tr>
														
				</table>
			
				<br> <br>
		
			</div>
			
		</div>
		
		<div class="innerContainer">
	
			<div class="accContainer">
		
				<%if((request.getAttribute("otherAcc"))!=null) { 
					JSONObject accs=(JSONObject) request.getAttribute("otherAcc"); 
					Iterator<String> keys = accs.keys();
				%>
				
				<p class="font2">Other Accounts</p>
			
				<%	while (keys.hasNext()) {
						String key = keys.next();
						JSONObject acc = (JSONObject) accs.get(key);
				%>

				<div class="individualAccContainer individualAccPadding">
					
					<table>

						<tr>
							<td class="font3">Account Number</td>
							<td class="font2"><%=acc.get("accNum")%></td>
						</tr>

						<tr>
							<td class="font3">Balance</td>
							<td class="font2">Rs. <%=acc.get("balance")%></td>
						</tr>

					</table>

					<form action="<%=request.getContextPath()%>/app/accounts/makePrimary" method="get">
					 	<input type="hidden" name="newAcc" value="<%=acc.get("accNum")%>">
						<button class="button-2" >Switch</button>
					</form>
				
				</div>
		
				<br>
			
				<% } 
				} else{%>
				
				<img src="<%=request.getContextPath()%>/images/CreateAccounts.svg" alt="CreateAccount" />
				
				<p class="font3">Create a new account by visiting your nearest BOB branch</p>
			
				<%}%>	

			</div>
		
		</div>
		
	</div>
	
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