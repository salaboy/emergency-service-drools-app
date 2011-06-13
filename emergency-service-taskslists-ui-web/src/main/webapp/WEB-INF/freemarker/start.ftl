<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"> 
	<script type="text/javascript"> 
	function getList()
	{
	    var name = document.getElementById('user').value;
	    var profile = document.getElementById('profile').value;
	
	    window.location="../list/"+name+"/"+profile;
	}
	</script> 
    <link rel="stylesheet" type="text/css" href="${rc.getContextPath()}/static/css/screen.css" /> 
 
</head> 
<body> 
<div class="wrap"> 
 
<div class="head">
    <h5>powered by plugtree</h2>
</div>
<h1> 
	Please select a user and profile.
</h1>
<table>
	<tbody><tr>
		<th><h3>User</h3></td> 
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
		<th><h3>Profile</h3></td> 
		<td>
			<select name="profile" id="profile">
				<option>Default</option>
				<option>Superuser</option>
			</select>
		</td>
	</tr>
	</tbody>
</table>
<button type="button" onclick="getList()">Get List</button>
</body>
</html>
	