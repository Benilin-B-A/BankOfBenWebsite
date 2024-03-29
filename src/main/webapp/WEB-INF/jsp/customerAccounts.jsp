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

	<jsp:include page="header.jsp" />
	
	<jsp:include page="customerNav.jsp" /> <br> <br> <br> <br> <br> <br>
	
	<div class="bodyContainer infoPadding">
	
		<div class="innerContainer">
		
		<div class="accContainer">
		
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
						<td class="font2"><%=account.get("type")%></td>
					</tr>
															
					<tr>
						<td class="font3">Account Status</td>
						<td class="font2"><%=account.get("status")%></td>
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
					<button class="button-2" >Make Primary</button>
				</form>
				
			</div>
			<br><br>
			<% } }
			else{%>
			<div class="innerContainer">
			
			</div>
			
			<%} %>	

		</div>
		
	</div>
	
	</div>
	
	
</body>

</html>