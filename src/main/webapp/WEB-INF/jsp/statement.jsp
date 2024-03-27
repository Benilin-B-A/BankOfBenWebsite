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
	<br>
	<br>

	<div>

		<table>

			<tr>
				<th>Transaction ID</th>
				<th>Type</th>
				<th>Amount</th>
				<th>Transaction Acc. No.</th>
				<th>Time</th>
				<th>Opening Balance</th>
				<th>Closing Balance</th>
				<!-- <th>Description</th> -->
			</tr>
			
		<%JSONArray transaction = (JSONArray) request.getAttribute("transactions"); 
		for (int i=0; i<transaction.length(); i++){
			JSONObject trans = (JSONObject) transaction.get(i);	
		%>
			<tr>
				<td><%=trans.get("transactionId") %></td>
				<td><%=trans.get("type") %></td>
				<td><%=trans.get("amount") %></td>
				<td><%=trans.get("transAccNum") %></td>
				<td><%=TimeUtil.getDateTime(trans.getLong("time")) %></td>
				<td><%=trans.get("openingBal") %></td>
				<td><%=trans.get("closingBal") %></td>
				<%-- <td><%=trans.get("description") %></td> --%>
			</tr>
		
		<%} %>

		</table>

	</div>

</body>

</html>