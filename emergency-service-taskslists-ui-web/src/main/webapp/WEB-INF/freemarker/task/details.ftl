Details:
<table class="sample">
	<tr>
		<#list taskInput?keys as keys>
			<td><b>${keys}</b></td>
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
</table>