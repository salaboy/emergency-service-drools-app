<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"> 
	<script type="text/javascript"> 
	function getList(name)
	{
	//    var name = document.getElementById('user').value;
	
	    window.location="../list/"+name+"/Default";
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
                     <img src="${rc.getContextPath()}/static/image/operator.png" onclick="getList('operator')"/>
                </td>
                <td>
                     <img src="${rc.getContextPath()}/static/image/control.png" onclick="getList('control')"/>
                </td>
                 <td>
                     <img src="${rc.getContextPath()}/static/image/garage_emergency_service.png" onclick="getList('garage_emergency_service')"/>
                </td>
                <td>
                     <img src="${rc.getContextPath()}/static/image/doctor.png" onclick="getList('doctor')"/>
                </td>
                <td>
                     <img src="${rc.getContextPath()}/static/image/firefighter.png" onclick="getList('firefighter')"/>
                </td>
               
	</tr>
	</tbody>
</table>

</body>
</html>
	