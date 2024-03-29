<%@page import="com.bank.util.TimeUtil"%>
<%@page import="com.bank.pojo.Transaction"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<title>Statement</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css">
</head>

<body>

	<jsp:include page="header.jsp" />
	<jsp:include page="customerNav.jsp" />
	<br>
	<br>

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
				<!-- <th>Description</th> -->
			</tr>
			
		<%JSONArray statements = (JSONArray) request.getAttribute("statements"); 
		for (int i=0; i<statements.length(); i++){
			JSONObject statement = (JSONObject) statements.get(i);	
		%>
			<tr>
				<td><%=statement.get("transactionId") %></td>
				<td><%=statement.get("type") %></td>
				<td><%=statement.get("amount") %></td>
				<td><%=statement.get("transAccNum") %></td>
				<td><%=TimeUtil.getDateTime(statement.getLong("time")) %></td>
				<td><%=statement.get("openingBal") %></td>
				<td><%=statement.get("closingBal") %></td>
				<%-- <td><%=trans.get("description") %></td> --%>
			</tr>
		
		<%} %>

		</table>

	</div>

</body>

</html>