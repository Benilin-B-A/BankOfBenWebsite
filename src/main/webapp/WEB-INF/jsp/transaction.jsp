<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>

<title>MoneyTransfer</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css">

</head>

<body>

	<jsp:include page="header.jsp" />
	
	<jsp:include page="customerNav.jsp" />
	
	<br><br><br><br>

	<div class="columnBodyContainer loginFormPadding">

		<div class="profileButtonContainer">
				
				<form action="<%=request.getContextPath()%>/app/transaction" method="get">
					<input type="hidden" name="transactionType" value="withinBank">
					<button class="button-2">Within Bank</button>
				</form>
			
				<form action="<%=request.getContextPath()%>/app/transaction" method="get">
					<input type="hidden" name="transactionType" value="toOtherBank">
					<button class="button-2">To Other Bank</button>
				</form>
				
		</div>
		<br> <br>
			
			<div class="transactionContainer">
				<form action="sendMoney" method="post">
				
				<div class="innerLoginFormContainer">

					<div>
					<input placeholder="Account Number" type="number" name="accNumber" />
					<br> <br>
				</div>

				<div>
					<input placeholder="Amount" type="number" name="amount" /> <br>
					<br>
				</div>
				<%
				if ((request.getAttribute("type")) != null) {
					if (request.getAttribute("type").equals("outside")) {
				%>
				<div>
					<input placeholder="IFSC" type="text" name="iFSC" /> <br> <br>
				</div>

				<input value="outside" type="hidden" name="type" />

				<%
				}
				} else {
				%>
				<input value="within" type="hidden" name="type" />
				<%
				}
				%>


				<div>
					<input placeholder="Description" type="text" name="description" />
					<br> <br>
				</div>

				<div>
					<input placeholder="T-PIN" type="password" name="tpin" /> <br>
					<br>
				</div>
					
					<div>
					
						<button type="submit" class="button-2">Send</button>
					
					</div>
				
				</div>
			
			</form>
			
			<img src="<%=request.getContextPath()%>/images/Transaction.svg" alt="TransferMoney">
		</div>
			
			
	</div>
			

</body>

</html>
