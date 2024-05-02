
	<div class="customerNavBarContainer navPadding">
	
	<%-- <a href="profile"> <img src="<%=request.getContextPath()%>/images/Profile.svg" alt="Profile"></a> --%>
	
	<a href="<%=request.getContextPath()%>/app/profile" class="link font1">Profile</a>

	<a href="<%=request.getContextPath()%>/app/accounts" class="link font1">Account</a>
	
	<a href="<%=request.getContextPath()%>/app/transaction" class="link font1">Send Money</a>
	
	<a href="<%=request.getContextPath()%>/app/statement" class="link font1">Statement</a>
	
	<a href="<%=request.getContextPath()%>/app/logout" class="link font1">Logout</a>
	
	<%-- <a href="<%=request.getContextPath()%>/app/logout"><button class="button-1">Logout</button></a> --%>
	
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