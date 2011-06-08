<html>
<head>
	<script type="text/javascript">
	function getList()
	{
	    var name = document.getElementById('user').value;
	    var profile = document.getElementById('profile').value;
	
	    window.location="../list/"+name+"/"+profile;
	}
	</script>
</head>
<body>
<h1> 
	Please select a user and profile.
</h1>
<table>
	<tr>
		<td>User:</td>
		<td>
			<select name="user" id="user">
				<option>operator</option>
				<option>control</option>
				<option>doctor</option>
				<option>garage_emergency_service</option>
			</select>
		</td>
	</tr>
	<tr>
		<td>Profile:</td>
		<td>
			<select name="profile" id="profile">
				<option>Default</option>
				<option>Superuser</option>
			</select>
		</td>
	</tr>	
</table>
<button type="button" onclick="getList()">Get List</button>
</body>
</html>
	