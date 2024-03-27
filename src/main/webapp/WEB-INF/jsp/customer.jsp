<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="com.bank.enums.AccountType" %>
<%@ page import="com.bank.enums.Status" %>
<%@page import="com.bank.pojo.Account" %>
<%@page import="java.util.Iterator" %>
<%@page import="com.bank.services.CustomerServices" %>
<%@page import="org.json.JSONObject" %>

<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Customer</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
 
</head>

<body>

	<div>
		
		<jsp:include page="header.jsp" />
		
		<div class="navPadding customerNavBarContainer">

			<div class="profileIconContainer">

				<a href="profile"> <img src="<%=request.getContextPath()%>/images/Profile.svg" alt="Profile"></a> 
				
				<a href="profile" class="link font1">
					<%-- <%= ((CustomerServices)request.getSession().getAttribute("user")).getName()%> --%>
				</a>
				
			</div>
										
			<a href="accounts" class="link font1">Account</a> 
										
			<a href="transaction/withinBank" class="link font1">Send Money</a> 
										
			<a href="statement" class="link font1">Statement</a> 
										
			<a href="logout"><button class="button-1">Logout</button></a>
									
		</div>
	
	</div>
	
	<br> <br>
									
	<% 
	if((request.getAttribute("tab"))!=null){
	String tab=(String) request.getAttribute("tab"); 
	if (tab.equals("profile")) {
		JSONObject customer=(JSONObject) request.getSession().getAttribute("customerDetails"); 
	%>

	<div class="bodyContainer">
	
		<div class="innerContainer">
	
				<div class="infoPadding contentContainer">
	
							<table class="table1">
						
								<tr>
									<td class="font3">Customer ID</td>
									<td class="font2"><%=customer.get("ID")%></td>
								</tr>
															
								<tr>
									<td class="font3">D:O:B</td>
									<td class="font2"><%=customer.get("DOB")%></td>
								</tr>
															
								<tr>
									<td class="font3">Phone</td>
									<td class="font2"><%=customer.get("phone")%></td>
								</tr>
															
								<tr>
									<td class="font3">Email</td>
									<td class="font2"><%=customer.get("mail")%></td>
								</tr>
															
								<tr>
									<td class="font3">Address</td>
									<td class="font2"><%=customer.get("address")%></td>
								</tr>
															
								<tr>
									<td class="font3">Gender</td>
									<td class="font2"><%=customer.get("gender")%></td>
								</tr>
															
								<tr>
									<td class="font3">Aadhar Number</td>
									<td class="font2"><%=customer.get("aadharNum")%></td>
								</tr>
															
								<tr>
									<td class="font3">Pan Number</td>
									<td class="font2"><%=customer.get("panNum")%></td>
								</tr>
															
								<tr>
									<td class="font3">No. Of Accounts</td>
									<td class="font2"><%=customer.get("noOfAcc")%></td>
								</tr>
														
							</table>
						
				</div>
				
				<br>
			
				<div class="profileButtonContainer">
						
					<a href="ChangePassword"><button class="button-2">Change Password</button></a> 
					
					<a href="ChangePin"><button class="button-2">Change TPIN</button></a>
												
				</div>
											
		</div>

											
		<div class="innerContainer">
												
			<img src="<%=request.getContextPath()%>/images/Bio.svg" alt="Bio">
											
		</div>
					
	</div>

	<% } else if (tab.equals("accounts")) { 
		JSONObject account=(JSONObject) request.getAttribute("account");
	%>
		
	<div class="bodyContainer">
												
		<div class="accContainer infoPadding ">
													
			<div class="contentContainer">
														
				<table class="table1">
															
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
						<td class="font3">Branch ID</td>
						<td class="font2"><%=account.get("branchId")%></td>
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
			
			</div>
			
			<br> <br>
		
		</div>

		<div class="accContainer infoPadding">
									
			<%if((request.getAttribute("otherAcc"))!=null) { 
				JSONObject accs=(JSONObject) request.getAttribute("otherAcc"); 
				Iterator<String> keys = accs.keys();
				while (keys.hasNext()) {
					String key = keys.next();
					JSONObject acc = (JSONObject) accs.get(key);
			%>
	
			<div class="contentContainer">
															
				<table>
				
					<tr>
						<td class="font3">Account Number</td>
						<td class="font2"><%acc.get("accNum");%></td>
					</tr>
																
					<tr>
						<td class="font3">Balance</td>
						<td class="font2"><%acc.get("balance");%></td>
					</tr>
															
				</table>
					
			</div>
														
			<div class="innerContainer">
															
				<a href="customer/makePrimary"><button class="button-2">Make Primary</button></a>
														
			</div>
			
			<% } } } }%>
							
		</div>

						
	</div>

</body>

</html>