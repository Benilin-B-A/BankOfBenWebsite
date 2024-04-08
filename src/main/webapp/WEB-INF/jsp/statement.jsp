<%@page import="com.bank.services.EmployeeServices"%>
<%@page import="com.mysql.cj.xdevapi.JsonArray"%>
<%@page import="com.bank.services.CustomerServices"%>
<%@page import="com.bank.util.TimeUtil"%>
<%@page import="com.bank.pojo.Transaction"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html>

<head>

	<title>Statement</title>
	
	<link rel="stylesheet"
		
	href="<%=request.getContextPath()%>/css/style.css">

</head>

<body>

	<%Object user = request.getSession().getAttribute("user");%>
	
	<jsp:include page="popUpScript.jsp" />
	
	<jsp:include page="header.jsp" />
	
	<%if (user instanceof CustomerServices){ %>
		
		<jsp:include page="customerNav.jsp" />
		
	<%}else if (user instanceof EmployeeServices){ %>
	
		<jsp:include page="employeeNav.jsp" />
	
	<%} else{%>
	
		<jsp:include page="adminNav.jsp" />
	
	<%}if (!(user instanceof CustomerServices)) {%>
	
	<br>
	
	<div class="buttonContainer">
	
		<form action="<%=request.getContextPath()%>/app/statement" method="get">
			<input type="text" placeholder="Account Number" name="accountNumber" min=1 step=1 required
				pattern="^[0-9]+$" title="Enter valid account number">
			<button type="submit" class="button-2">View</button>
		</form>
		
	</div>
	
	<%}JSONObject statementObject = (JSONObject) request.getAttribute("statements");
	JSONArray statements = null;
	if (statementObject != null){
		statements = statementObject.getJSONArray("transactionArray"); 
	}
	if(statements != null && statements.length()!=0){
		Object obj = request.getSession().getAttribute("user");
		Long accNum = Long.parseLong((String)request.getAttribute("accNum"));
	%>
	
	<p class="font2">Account Number : <%=accNum%></p>
	
	<div>
		
		<table class="table-format1">

			<tr class="font2 ">
				<th>Transaction ID</th>
				<th>Type</th>
				<th>Amount</th>
				<th>Transaction Acc. No.</th>
				<th>Time</th>
				<th>Opening Balance</th>
				<th>Closing Balance</th>
				<th>Description</th>
			</tr>
			
			<%for (int i=0; i<statements.length(); i++){
				JSONObject statement = (JSONObject) statements.get(i);	 %>
			<tr>
				<td><%=statement.get("transactionId") %></td>
				<td><%=statement.get("type") %></td>
				<td><%=statement.get("amount") %></td>
				<%Long transAccNum = (Long) statement.get("transAccNum");
				if(transAccNum == 0){%>
					<td>Nil</td>
				<%}else { %>
					<td><%=transAccNum%></td>
				<%} %>
				<td><%=TimeUtil.getDateTime(statement.getLong("time")) %></td>
				<td><%=statement.get("openingBal") %></td>
				<td><%=statement.get("closingBal") %></td>
				<%String description = (String) statement.get("description");
				if(description == null){%>
					<td>Nil</td>
				<%}else { %>
					<td><%=description%></td>
				<%} %>
			</tr>
		
			<%} %>
		
		</table>

	</div>
	
	<br>
	
	<%Object pageNos = statementObject.get("pages");
	
	if (pageNos != null){ 
	
	%>
		
	<div class="profileButtonContainer">
	
	<%	int pages = ((Double) pageNos).intValue();
		
		if (pages>1){
		
			for(int i=1;i<=pages;i++){%>
		
		<form action="statement">
		
			<input type="hidden" name="accountNumber" value="<%=accNum%>">
		
			<button class="button-2" type="submit" name="pageNo" value="<%=i%>"><%=i%></button>
		
		</form>
	
	<%		}
		} 
	%>
		
	</div>
	
	<%} %>
	
	<% }else {
			
		if(user instanceof CustomerServices){%>
		
		<br><br><br>
			
		<div class="columnBodyContainer">
			
			<img src="<%=request.getContextPath()%>/images/NoStatements.svg" alt="customerDetails">
		
			<p class="font2">No statements to view yet :(</p>
		
		</div>
			
		<%}else{ %>
			
		<div class="columnBodyContainer">
			
			<img src="<%=request.getContextPath()%>/images/SearchStatements.svg" alt="customerDetails">
		
			<p class="font2">Search to view account statements</p>
		
		</div>
			
		<%}
	
	} %>
	
	<%String message = (String) request.getAttribute("errorMessage");
		if( message != null) {%>
				
	<div class="messageContainer">
		<p class="errorMessage" id="msg"><%=message%></p>
	</div>
	
	<%}	%>

</body>

</html>