<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="com.bank.enums.AccountType"%>
<%@ page import="com.bank.enums.Status"%>
<%@page import="com.bank.pojo.Account"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.bank.services.CustomerServices"%>
<%@page import="org.json.JSONObject"%>

<!DOCTYPE html>

<html>

<head>

	<meta charset="UTF-8">

	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<title>Profile</title>

	<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
 
</head>

<body>

	<div>
	
		<jsp:include page="popUpScript.jsp" />
		
		<jsp:include page="header.jsp" />
		
		<jsp:include page="customerNav.jsp" />
	
	</div>  
	
	<br><br><br><br>

	<div class="columnBodyContainer loginFormPadding">
	
		<div class="transactionContainer ">
		
			<%Object obj = request.getSession().getAttribute("user");
				JSONObject profile = (JSONObject) request.getAttribute("profile"); 
			%>
			
			<table class="table-format2">
						
				<tr>
					<td class="font3">User ID</td>
					<td class="font2"><%=profile.get("ID")%></td>
				</tr>
								
				<tr>
					<td class="font3">Name</td>
					<td class="font2"><%=profile.get("name")%></td>
				</tr>
															
				<tr>
					<td class="font3">D:O:B</td>
					<td class="font2"><%=profile.get("DOB")%></td>
				</tr>
															
				<tr>
					<td class="font3">Phone</td>
					<td class="font2"><%=profile.get("phone")%></td>
				</tr>
															
				<tr>
					<td class="font3">Email</td>
					<td class="font2"><%=profile.get("mail")%></td>
				</tr>
															
				<tr>
					<td class="font3">Address</td>
					<td class="font2"><%=profile.get("address")%></td>
				</tr>
				
				<%if (obj instanceof CustomerServices) {%>
															
					<tr>
						<td class="font3">Aadhar Number</td>
						<td class="font2"><%=profile.get("aadharNum")%></td>
					</tr>
																
					<tr>
						<td class="font3">Pan Number</td>
						<td class="font2"><%=profile.get("panNum")%></td>
					</tr>
																
					<tr>
						<td class="font3">No. Of Accounts</td>
						<td class="font2"><%=profile.get("noOfAcc")%></td>
					</tr>
					
				<%} else {%>

					<tr>
						<td class="font3">Branch ID</td>
						<td class="font2"><%=profile.get("branchId")%></td>
					</tr>				
				
				<%} %>
														
			</table>
	
			<div class="innerLoginFormContainer">
				
				<img src="<%=request.getContextPath()%>/images/Bio.svg" alt="Bio">
			
				<div class="profileButtonContainer">
				
					<a href="<%=request.getContextPath()%>/app/changePassword">
						<button class="button-2">Change Password</button>
					</a> 
					
					<%if (obj instanceof CustomerServices) {%>
					
						<a href="<%=request.getContextPath()%>/app/changePin">
							<button class="button-2">Change TPIN</button>
						</a>
						
					<%} %>
			
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