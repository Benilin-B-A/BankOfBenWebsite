	<div class=" customerNavBarContainer navPadding">
	
		<a href="<%=request.getContextPath()%>/app/profile" class="link font1">Profile</a>
		
		<a href="<%=request.getContextPath()%>/app/customerDetails" class="link font1">Manage Customers</a>
	
		<a href="<%=request.getContextPath()%>/app/accountDetails" class="link font1">Manage Accounts</a>
		
		<a href="<%=request.getContextPath()%>/app/transaction" class="link font1">Transaction</a>
		
		<a href="<%=request.getContextPath()%>/app/statement" class="link font1">Statement</a>
		
		<a href="<%=request.getContextPath()%>/app/logout" class="link font1">Logout</a>
	
	</div>

<script>

document.addEventListener("DOMContentLoaded", function() {
    var links = document.querySelectorAll('.customerNavBarContainer a');
    var currentUrl = window.location.href;
    links.forEach(function(link) {
        if (link.href === currentUrl) {
            link.classList.add('active');
        }
    });
});

</script>