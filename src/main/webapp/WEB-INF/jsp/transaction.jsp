<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>

<title>TransactionPage</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css">

</head>

<body>

	<jsp:include page="header.jsp" />

	<div>

		<div>
			<a href="withinBank"><button>Within Bank</button></a> <a
				href="outsideBank"><button>To Other Bank</button></a>
		</div>
		<br> <br>

		<div>

			<form action="sendMoney" method="post">
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
					<input placeholder="description" type="text" name="description" />
					<br> <br>
				</div>

				<div>
					<input placeholder="T-PIN" type="password" name="tpin" /> <br>
					<br>
				</div>

				<div>
					<button type="submit">Send</button>
				</div>

			</form>

		</div>

		<%-- <%
		} else if (request.getAttribute("type").equals("outside")) {
		%>

		<div>

			<form action="sendMoneyOutside" method="post">

				<div>
					<input placeholder="Account Number" type="number" name="accNumber" /><br> <br>
				</div>

				<div>
					<input placeholder="Amount" type="number" name="amount" /><br>
					<br>
				</div>

				<div>
					<label for="ifsc">IFSC </label> <input type="text" id="ifsc" /><br>
					<br>
				</div>

				<div>
					<input placeholder="description" type="text" name="description" /><br> <br>
				</div>

				<div>
					<button type="submit">Send</button>
				</div>

			</form>


		</div>

		<%
		}
		%> --%>
	</div>

</body>

</html>
