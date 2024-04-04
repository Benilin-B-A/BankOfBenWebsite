<script>
	document.addEventListener("DOMContentLoaded", function() {
		var message = document.getElementById("msg");

		message.style.opacity = 1;

		setTimeout(function() {
			message.style.opacity = 0;
		}, 1500);
	});
</script>