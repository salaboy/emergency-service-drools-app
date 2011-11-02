<table>
	<tbody><tr>
		<#list taskInput?keys as keys>
			<th><h3>${keys}</h3></th> 
		</#list>
	</tr>
	  <tr>
		<#list taskInput?keys as keys>
			<td id='td_${keys}'>
			<#if taskInput[keys]?is_sequence>
				<select name="div_${keys}" id="user">
					<#list taskInput[keys] as sequenceItem>
						<option>
							${sequenceItem}
						</option>
					</#list>
				</select>
			<#else>
				<#if taskInput[keys]?is_date>
				<div id = 'div_${keys}'>${taskInput[keys]?string('ddMMaaa')}</div>
				<#else>
				<div id = 'div_${keys}'>${taskInput[keys]}</div>
				</#if>
			</#if>
			</td>
		</#list>
		</tr>
		</tbody>
</table>