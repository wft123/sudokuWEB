<%@page import="com.roxy.sudoku.SudokuLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	SudokuLogic logic = (SudokuLogic)request.getAttribute("logic");
	int[] originalMap = null;
	int[] blindMap = null;
	if(logic!=null){
		int level = Integer.parseInt(request.getParameter("level"));
		logic.autoMakeMap(level);
		originalMap = logic.getOriginalMap();
		blindMap = logic.getBlindMap();
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="//code.jquery.com/jquery-1.12.0.min.js"></script>
<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<title>Insert title here</title>
<style>
body{ font:0.75em "맑은 고딕", 돋움, 굴림; text-align:center }
#container{
	width: 340px;
	margin: 0 auto;
	padding-top: 100px;
}
#sudoku > div > input {
	width: 30px;
	height: 30px;
	text-align: center;
}
#sudoku > div {
	float: left;
	border : 1px solid;
}
#panel3, #panel6, #etc{
	clear:both;
}
</style>
<script>
$(function(){
	var gap = 0;
	var pGap = 0;
	var pIndex = 0;
	
	var bm = new Array();
	var om = new Array();
	
	<%
	if(logic!=null){
		for(int i = 0;i < logic.totalCell ; i++){
			%>om.push(<%=originalMap[i]%>);<%			
			%>bm.push(<%=blindMap[i]%>);<%			
		}
	}
	%>
	
	for(i=0; i  < 9 ; i++){
		$('#sudoku').append("<div id='panel"+i+"'></div>");
		if((i+1)%3==0) $('#sudoku').append("<br/>");
	}
	
	for(i=0 ; i < 81 ; i++){
		if( i!=0 && i%9==0 ) gap += 3;
		
		if( i!=0 && i%27==0 ){
			gap=0; 
			pGap += 6;
		}
		
		pIndex = Math.floor(i/3)-(gap+pGap);
		$('#panel'+pIndex).append("<input type='text' id='cell"+i+"'>");
		if((i+1)%3==0) $('#panel'+pIndex).append("<br/>");
	}
	
	var setNumberField = function(index, number, color){
		$('#cell'+index).val(number);
		$('#cell'+index).css("background-color",color);
		$('#cell'+index).attr("readonly",true);
	}
	
	if(bm !=null){
		for(i = 0; i <bm.length; i++){
			if(bm[i]!=0){
				setNumberField(i,bm[i],"cyan");
			}
		}
	}
	
	$("#check").click(function(){
		checked =0 ;
		for(i=0; i <bm.length ; i++){
			if($('#cell'+i).val()==om[i]){
				setNumberField(i,om[i],"yellow");
			}else{
				checked++;
				$('#cell'+i).val("");
			}
		}		
		if(checked ==0 ){
			alert("정답입니다!!!!!");
			return;
		}
		for(i = 0; i <bm.length; i++){
			if(bm[i]!=0){
				setNumberField(i,bm[i],"cyan");
			}
		}
	});
	
	
	
});

</script>
</head>
<body>
	<div id = "container">
		<div id="sudoku"></div>
		<div id="etc">
			<br><br>
			<form action="${pageContext.request.contextPath }/logic" method="get">
				<input type="radio" name="level" checked="checked" value=0>Lv 1
				<input type="radio" name="level" value=1>Lv 2
				<input type="radio" name="level" value=2>Lv 3 <br/>
				<input type="submit" id = "start"  value="Start">
				<input type="button" id = "check"  value="Check">
			</form>
		</div>
	</div>
</body>
</html>