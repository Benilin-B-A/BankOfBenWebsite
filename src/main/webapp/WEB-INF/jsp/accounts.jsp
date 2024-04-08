<%@page import="com.bank.services.EmployeeServices"%>
<%@page import="com.bank.pojo.Branch"%>
<%@page import="java.util.List"%>
<%@page import="com.bank.util.TimeUtil"%>
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

		
			<%Object user = request.getSession().getAttribute("user");%>
		
		<jsp:include page="header.jsp" />
		
		<%if (user instanceof EmployeeServices){ %>
	
		<jsp:include page="employeeNav.jsp" />
	
	<%} else{%>
	
		<jsp:include page="adminNav.jsp" />
	
	<%}%>
		
		<jsp:include page="popUpScript.jsp" />
		<br>
		
	
	<%String tab = (String) request.getAttribute("tab");
	
	if(tab.equals("viewAccount")){ %>
		
		
		<div class="searchBoxContainer">
		
		<div>
			
			<a href="<%=request.getContextPath()%>/app/createAccount" class="link">
				<button class="button-2">Create New Account</button>
			</a>
			
		</div>
		
		<%if((request.getSession().getAttribute("user"))instanceof AdminServices){ %>
			<form action="<%=request.getContextPath()%>/app/accountDetails" method="get">
  			<label for="type" class="font3">Search By :</label>
 			 	<select name="type" id="searchType">
  				 	 <option value="accountNumber">Account Number</option>
    				 <option value="customerID">Customer ID</option>	
 				 </select>
			<input type="text" placeholder="CustomerID/AccountNum" name="value" pattern="^[0-9]+$" 
				title="Enter a valid input" required>
			<button type="submit" class="button-2">View</button>
		</form>
		<% }else{%>
		
		<form action="<%=request.getContextPath()%>/app/accountDetails" method="get">
			<input type="number" placeholder="Account Number" name="accountNumber">
			<button type="submit" class="button-2">View</button>
		</form>
		
		<%} %>
		
		
	</div>

				<%
				JSONObject account=(JSONObject) request.getAttribute("account");
				JSONObject accounts=(JSONObject) request.getAttribute("allAccounts");
				if (account != null){
				%>
				<div class="columnBodyContainer">

				<div class="transactionContainer loginFormPadding">
	
																
				<table class="table-format2">
				
					<%if (account.getBoolean("primary")==true){ %>
					<tr>
						<td class="font2">Primary Account</td>
					</tr>
					<%} %>
					
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
						<td class="font3">Opened On</td>
						<td class="font2"><%=TimeUtil.getDateTime(account.getLong("openedOn"))%></td>
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
	
	<br>
	
	<%	
							if(status == 1){%>
								<form action="setAccStatus">
									<input type="hidden" name="iD" value="<%=account.get("accNum")%>">
									<input type="hidden" name="action" value="deactivate">
									<button type="submit" class="button-2">Deactivate</button>
								</form>
							<%}else{%>
								<form action="setAccStatus">
									<input type="hidden" name="iD" value="<%=account.get("accNum")%>">
									<input type="hidden" name="action" value="activate">
									<button type="submit" class="button-2">Activate</button>
								</form>
							<%} %>	
	
	</div>
	<%}else if(accounts !=null){ %>
	
	<div class="container">
	
		<h2 >Customer ID : <%=request.getAttribute("customerID")%></h2>
	
		<p class="font2"> No. Of Accounts : <%=accounts.length()%></p>
	
	</div>
		
	<%
		Iterator<String> keys = accounts.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			JSONObject acc = (JSONObject) accounts.get(key);%>
			
			
			<div class="columnBodyContainer">
			
				<div class="transactionContainer loginFormPadding">
	
																
				<table class="table-format2">
				
					<%if (acc.getBoolean("primary")){ %>
					<tr>
						<td class="font2">Primary Account</td>
					</tr>
					<%} %>
					
					<tr>
						<td class="font3">Account Number</td>
						<td class="font2"><%=acc.get("accNum")%></td>
					</tr>
															
					<tr>
						<td class="font3">Balance</td>
						<td class="font2">Rs. <%=acc.get("balance")%></td>
					</tr>
															
					<tr>
						<td class="font3">Branch</td>
						<td class="font2"><%=acc.get("branch")%></td>
					</tr>
															
					<tr>
						<td class="font3">Account Type</td>
						<td class="font2">
							<%
							JSONObject typeObj = acc.getJSONObject("type");
							int type = typeObj.getInt("type");
							if(type == 1){%>
								Current
								
							<%} else{%>
								Savings
							
							<%} %>
								
						</td>
					</tr>
					
					<tr>
						<td class="font3">Opened On</td>
						<td class="font2"><%=TimeUtil.getDateTime(acc.getLong("openedOn"))%></td>
					</tr>
					
					<tr>
						<td class="font3">Status</td>
						<td class="font2">
							<%
							JSONObject statusObj = acc.getJSONObject("status");
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
	<br>
	
							<%	
							if(status == 1){%>
								<form action="setAccStatus">
									<input type="hidden" name="iD" value="<%=acc.get("accNum")%>">
									<input type="hidden" name="action" value="deactivate">
									<button type="submit" class="button-2">Deactivate</button>
								</form>
							<%}else{%>
								<form action="setAccStatus">
									<input type="hidden" name="iD" value="<%=acc.get("accNum")%>">
									<input type="hidden" name="action" value="activate">
									<button type="submit" class="button-2">Activate</button>
								</form>
							<%} %>	
	
	</div>
	
	
	<%}} else{ %>
	
	<br><br><br><br>
	
	<div class="columnBodyContainer">
			<img src="<%=request.getContextPath()%>/images/SearchAccounts.svg" alt="customerDetails">
			<p class="font2">Search to view account details</p>
	</div>
	
	<%}
	
	} 
	
	else if (tab.equals("createAccount")){%>
		<br><br>
		
		<div class="columnBodyContainer">
		
		<div class="transactionContainer loginFormPadding">
						
						<form action="createAccount" class="innerLoginFormContainer" method="post">
						
							<table class="table-format2">
								
								<tr>
									<td class="font3" style="width:40%">Customer ID</td>
									<td class="font2" ><input type="number" name="customerID" /></td>
								</tr>
								
								<tr>
									<td class="font3">Account Type</td>
									<td style="width:40%"><input type="radio" id="current" name="accType" value="Current">
														<label for="current">Current</label>
  													  <input type="radio" id="savings" name="accType" value="Savings">
														<label for="savings">Savings</label>
									</td>
								</tr>
								<%if(request.getSession().getAttribute("user") instanceof AdminServices){ %>
								<tr>
								<td>Branch</td>
								<td>
								
									<select name="branchId">
									
									<%List<Branch> list = (List<Branch>) request.getAttribute("branches");
									for(Branch branch : list){%>
  				 	 					<option value="<%=branch.getId()%>"><%=branch.getBranchName()%></option>
    									 <%} %>
 									 </select>
								</td>
									
								</tr>
								<%}%>
								
							</table>
							
							<button type="submit" class="button-2">Submit</button>
							
							<br><br>
							
						</form>
				</div>		
				<br><br>
					<a href="<%=request.getContextPath()%>/app/accountDetails" class="link font1">
						<button class="button-2">Cancel</button>
					</a>
		</div>
	

				
	<%}
	String msg = (String) request.getAttribute("successMessage");
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