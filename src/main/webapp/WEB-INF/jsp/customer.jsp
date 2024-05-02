<%@page import="com.bank.services.EmployeeServices"%>
<%@page import="com.bank.pojo.Branch"%>
<%@page import="java.util.List"%>
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

	<div class="bContainer">
	
	<div class="bContainer">
		
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
	
	if(tab.equals("viewCustomer")){ %>
		
		
	<div class="searchBoxContainer">
		
		<div>
			
			<a href="<%=request.getContextPath()%>/app/addCustomer" class="link">
				<button class="button-2">Add New Customer</button>
			</a>
			
		</div>
		
		<form action="<%=request.getContextPath()%>/app/customerDetails" method="get">
			<input type="text" placeholder="Customer ID" name="customerID" required
				pattern="^[1-9]{1}[0-9]{0,15}$" title="Enter valid Customer ID">
			<button type="submit" class="button-2">View</button>
		</form>
	</div>

	<br>

	<%	
		JSONObject customer =  (JSONObject) request.getAttribute("customerDetails"); 
		if (customer!=null){
	%>
	
	<div class="columnBodyContainer">

				<div class="transactionContainer loginFormPadding">
	
							<table class="table-format2">
						
								<tr>
									<td class="font3">Customer ID</td>
									<td class="font2"><%=customer.get("ID")%></td>
								</tr>
								
								<tr>
									<td class="font3">Name</td>
									<td class="font2"><%=customer.get("name")%></td>
								</tr>
															
								<tr>
									<td class="font3">D:O:B</td>
									<td class="font2"><%=customer.get("DOB")%></td>
								</tr>
								
								<tr>
									<td class="font3">Gender</td>
									<td class="font2"><%=customer.get("gender")%></td>
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
									<td class="font2">
										<%
											String address = (String) customer.get("address");
											String[] addressArr = address.split(",");
											for(String str : addressArr){%>
												<%=str%>
												<br><br>			
											<%}
										%>
									</td>
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
								
								<tr>
									<td class="font3">Status</td>
									<td class="font2">
							<%
							JSONObject statusObj = customer.getJSONObject("status");
							int status = statusObj.getInt("state");
							if(status == 1){%>
								Active
								
							<%} else if(status ==2) {%>
								Inactive
							
							<%} else{%>
								Blocked
							
							<%} %>
								</td>
								
								<td>
								
								<%
							if(status == 1){%>
								<form action="setStatus" method="post">
									<input type="hidden" name="iD" value="<%=customer.get("ID")%>">
									<input type="hidden" name="action" value="deactivate">
									<button type="submit" class="button-2">Deactivate</button>
								</form>
							<%}else{%>
								<form action="setStatus" method="post">
									<input type="hidden" name="iD" value="<%=customer.get("ID")%>">
									<input type="hidden" name="action" value="activate">
									<button type="submit" class="button-2">Activate</button>
								</form>
							<%} %>	
								
								</td>
								</tr>
								</table>
								
								
				</div>
				<br>
				
				<div class="profileButtonContainer">
				
				<form action="<%=request.getContextPath()%>/app/accountDetails" method="post">
					<input type="hidden" name="type" value="customerID">
					<input type="hidden" name="value" value="<%=customer.get("ID")%>">
					<button type="submit" class="button-2">View Accounts</button>
				</form>
								
				<%-- <form action="<%=request.getContextPath()%>/app/updateCustomer">
					<input type="hidden" name="customerID" value="<%=customer.get("ID")%>">
					<button type="submit" class="button-2">Update</button>
				</form> --%>
				
				</div>
								
		<br><br>	
	</div>
	
	<%} else{ %>
	
	<div class="columnBodyContainer">
			<img src="<%=request.getContextPath()%>/images/CustomerDetails.svg" alt="customerDetails">
			<p class="font2">Search to view customer details</p>
	</div>
	
	<%}
	
	} 
	
	else if (tab.equals("addCustomer")){%>
	
	<div class="buttonContainer">
		
		<a href="<%=request.getContextPath()%>/app/customerDetails" class="link font1">
				<button class="button-2">Cancel</button>
		</a>
		
	</div>
	
			<div class="columnBodyContainer">

				<div class="transactionContainer loginFormPadding">
						
						<form action="addCustomer" class="innerLoginFormContainer" method="post">
						
							<table class="table-format2">
								
								<tr>
									<td class="font3" style="width:40%">Name</td>
									<td class="font2" ><input placeholder="First Name" type="text" name="name" required
										pattern="^[a-zA-Z]{1,20}$" title="Name cannot contain any numbers or special characters"/></td>

									<td class="font3" >D.O.B</td>
									<td class="font2" ><input placeholder="YYYY-MM-DD" type="date" name="dOB" required
									min='1920-01-01' max='2020-12-31'/></td>
								</tr>
									
								<tr>
									<td></td>
									<td class="font2" ><input placeholder="Last Name" type="text" name="lName" required
										pattern="^[a-z A-Z]{1,20}$" title="Name cannot contain any numbers or special characters"/></td>
								</tr>
								
								<tr>
									<td class="font3">Phone</td>
									<td class="font2"><input placeholder="xxxxxxxxxx" type="text" name="phone" required
										pattern="^[7-9]{1}[0-9]{9}$" title="Enter valid Phone number"/></td>

									<td class="font3">E-Mail</td>
									<td class="font2"><input placeholder="xxx@yyy.com" type="email" name="eMail" required/></td>
								</tr>
															
								<tr>
									<td class="font3" >Gender</td>
									<td style="width:40%"><input type="radio" id="male" name="gender" value="Male" required>
														<label for="male">Male</label>
  													  <input type="radio" id="female" name="gender" value="Female" required>
														<label for="female">Female</label>
  													  <input type="radio" id="other" name="gender" value="Other" required>
														<label for="other">Other</label>
									</td>
								</tr>
								<tr>
									<td class="font3">Address</td>
									<td><input placeholder="Line 1" type="text" name="addressL1" required
									pattern="^[a-zA-Z0-9 ]{1,20}$" title="Enter valid address"/></td>
									
									<td class="font3">Pincode</td>
									<td><input type="text" name="pincode" pattern="^[0-9]+$" title="Enter valid PinCode"
											required pattern="^[0-9]{6}$" title="Enter valid PinCode"/></td>
								</tr>
								
								<tr>
									<td class="font3"></td>
									<td><input placeholder="Line 2" type="text" name="addressL2" required
									pattern="^[a-zA-Z0-9 ]{1,20}$" title="Enter valid address"/></td>
								</tr>
															
								<tr>
									<td class="font3">Aadhar Number</td>
									<td class="font2"><input placeholder="xxxx xxxx xxxx" type="text" name="aadharNumber" 
										pattern="^[0-9]{12}$" title="Enter valid Aadhar Number" required></td>

									<td class="font3" style="width:40%">Pan Number</td>
									<td class="font2" style="width:40%"><input placeholder="XXXXXYYYY" type="text" name="panNumber" 
									pattern="^[A-Z]{5}[0-9]{4}[A-Z]$" title="Enter valid PAN number" required/></td>
								</tr>
															
								<tr>
									<td class="font3">Account Type</td>
									<td>
									<select name="accountType">
										<option value="Current">Current</option>
										<option value="Savings">Savings</option>									
									</select>
									</td>
								</tr>
								
								<%if((request.getSession().getAttribute("user")) instanceof AdminServices){ %>
								<tr>
								<td class="font3">Branch</td>
								<td>
								
									<select name="branchId">
									
									<%List<Branch> list = (List<Branch>) request.getAttribute("branches");
									for(Branch branch : list){%>
  				 	 					<option value="<%=branch.getId()%>"><%=branch.getBranchName()%></option>
    									 <%} %>
 									 </select>
								</td>
									
								</tr>
								
								<%} %>
								
							</table>
							
							<div class="profileButtonContainer">
							
							<button type="submit" class="button-2">Submit</button>
							
							</div>
							
							<br>
							
						</form>
						
				</div> <br><br>	
					
	</div>
	
	<%}else if(tab.equals("updateCustomer")){%>
		
		<div class="buttonContainer">
		
		<a href="<%=request.getContextPath()%>/app/customerDetails" class="link font1">
				<button class="button-2">Cancel</button>
		</a>
		
	</div>
	
	<br><br><br><br><br>

	<%	
		JSONObject customer =  (JSONObject) request.getAttribute("customerDetails");
		if (customer!=null){
	%>
	
	<div class="columnBodyContainer">

				<div class="transactionContainer loginFormPadding">
				
					<form action="updateCustomer" class="innerLoginFormContainer" method="post">
					
					<table class="table-format2">
						
								<tr>
									<td class="font3">Customer ID</td>
									<td class="font2"><%=customer.get("ID")%></td>
								</tr>
								
								<tr>
									<td class="font3">Name</td>
									<td class="font2">
									<input placeholder="<%=customer.get("name")%>" type="text" name="name" required
										pattern="^[a-zA-Z]+$" title="Name cannot contain any numbers or special characters"/>
									</td>
								</tr>
															
								<tr>
									<td class="font3" >D.O.B</td>
									<td class="font2" ><input placeholder="YYYY-MM-DD" type="date" name="dOB" required/></td>
								</tr>
								
								<tr>
									<td class="font3">Gender</td>
									<td class="font2"><%=customer.get("gender")%></td>
								</tr>
															
								<tr>
									<td class="font3">Phone</td>
									<td class="font2"><input placeholder="<%=customer.get("phone")%>" type="text" name="phone" required
										pattern="^[7-9]{1}[0-9]{9}$" title="ENter valid Phone number"/></td>
								</tr>
															
								<tr>
									<td class="font3">Email</td>
									<td class="font2"><input placeholder="<%=customer.get("mail")%>" type="email" name="eMail" required/></td>
								</tr>
															
								<tr>
									<td class="font3">Address</td>
									<td><input placeholder="<%=customer.get("address")%>" type="text" name="addressL1" required/></td>
								</tr>
															
								<tr>
									<td class="font3">Aadhar Number</td>
									<td class="font2"><input placeholder="<%=customer.get("aadharNum")%>" type="number" name="aadharNumber" 
										pattern="^[0-9]{12}$" title="Enter valid Aadhar Number" required></td>
								</tr>
															
								<tr>
									<td class="font3">Pan Number</td>
									<td class="font2"><input placeholder="<%=customer.get("panNum")%>" type="text" name="panNumber" 
									pattern="^[0-9A-Z]+$" title="Enter valid PAN number" required/></td>
								</tr>
															
								<tr>
									<td class="font3">No. Of Accounts</td>
									<td class="font2"><%=customer.get("noOfAcc")%></td>
								</tr>
								
								<tr>
									<td class="font3">Status</td>
									<td class="font2">
							<%
							JSONObject statusObj = customer.getJSONObject("status");
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
						
								
							<div class="profileButtonContainer">
							
							<button type="submit" class="button-2">Submit</button>
							
							</div>	
							
							<br>
							
					
					</form>
	
							
								
								
				</div>
				<br>
				
		<br><br>	
	</div>
		
		
	<%}}
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
		
		</div>
								
	
	</div>
			
</body>

</html>