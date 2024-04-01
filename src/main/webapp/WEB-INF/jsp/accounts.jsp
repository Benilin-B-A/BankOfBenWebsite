<%@page import="com.bank.services.AdminServices"%>
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
<title>Manage Customer</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
 
</head>

<body>

		
		<jsp:include page="header.jsp" />
		
		<jsp:include page="employeeNav.jsp" />
		<br>
		
	
	<%String tab = (String) request.getAttribute("tab");
	
	if(tab.equals("viewAccount")){ %>
		
		
		<div class="customerNavBarContainer">
		
		<div>
			
			<a href="<%=request.getContextPath()%>/app/createAccount" class="link">
				<button class="button-2">Create New Account</button>
			</a>
			
		</div>
		
		<form action="<%=request.getContextPath()%>/app/accountDetails" method="get">
  			<label for="type" class="font3">Search By :</label>
 			 	<select name="type" id="searchType">
  				 	 <option value="accountNumber">Account Number</option>
    				 <option value="CustomerID">Customer ID</option>	
 				 </select>
			<input type="number" placeholder="CustomerID/AccountNum" name="value">
			<button type="submit" class="button-2">View</button>
		</form>
		
	</div>

	<br> <br> <br><br>

				<%
				JSONObject account=(JSONObject) request.getAttribute("account");
				%>
				<div class="columnBodyContainer">

				<div class="transactionContainer loginFormPadding">
	
																
				<table class="table-format2">
					
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
	</div>
	
	</div>
	
	<%} else{ %>
	
	<div class="columnBodyContainer">
			<img src="<%=request.getContextPath()%>/images/SearchAccounts.svg" alt="customerDetails">
			<p class="font2">Search to view account details</p>
	</div>
	
	<%}
	
	} 
	
	else if (tab.equals("addCustomer")){%>
	
	<br><br>
	
			<div class="columnBodyContainer">

				<div class="transactionContainer loginFormPadding">
						
						<form action="addCustomer" class="innerLoginFormContainer" method="post">
						
							<table class="table-format2">
								
								<tr>
									<td class="font3" style="width:40%">Name</td>
									<td class="font2" ><input placeholder="FirstName_LastName" type="text" name="name" /></td>

									<td class="font3" >D.O.B</td>
									<td class="font2" ><input placeholder="YYYY-MM-DD" type="date" name="dOB" /></td>
								</tr>
								
								<tr>
									<td class="font3">Phone</td>
									<td class="font2"><input placeholder="xxxxxxxxxx" type="number" name="phone" /></td>

									<td class="font3">E-Mail</td>
									<td class="font2"><input placeholder="xxx@yyy.com" type="text" name="eMail" /></td>
								</tr>
															
								<tr>
									<td class="font3">Gender</td>
									<td style="width:40%"><input type="radio" id="male" name="gender" value="Male">
														<label for="male">Male</label>
  													  <input type="radio" id="female" name="gender" value="Female">
														<label for="female">Female</label>
  													  <input type="radio" id="other" name="gender" value="Other">
														<label for="other">Other</label>
									</td>
								</tr>
								<tr>
									<td class="font3">Address</td>
									<td><input placeholder="Town, District, State, Pincode" type="text" name="address" /></td>
								</tr>
															
								<tr>
									<td class="font3">Aadhar Number</td>
									<td class="font2"><input placeholder="xxxx xxxx xxxx" type="number" name="aadharNumber" /></td>

									<td class="font3" style="width:40%">Pan Number</td>
									<td class="font2" style="width:40%"><input placeholder="XXXXXYYYY" type="text" name="panNumber" /></td>
								</tr>
															
								<tr>
									<td class="font3">Account Type</td>
									<td><input type="radio" id="current" name="accountType" value="Current">
														<label for="current">Current</label>
  													  <input type="radio" id="savings" name="accountType" value="Savings">
														<label for="savings">Savings</label>
									</td>
								</tr>
								
								<%if((request.getSession().getAttribute("user")) instanceof AdminServices){ %>
								<tr>
									<td class="font3">Branch ID</td>
									<td><input placeholder="" type="number" name="branchID" /></td> 
								</tr>
								
								<%} %>
								
							</table>
							
							<button type="submit" class="button-2">Submit</button>
							
						</form>
				</div>		
	</div>
	<%} %>
									
</body>

</html>