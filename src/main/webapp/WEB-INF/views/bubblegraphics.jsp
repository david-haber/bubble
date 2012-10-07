<html>
<title>Bubble Graphics</title>
<body>

	<applet code='bubbles.class' codebase='resources/graphics/bubble'
		archive='bubbles.jar,SQLibrary.jar,mysql-connector-java-3.1.14-bin.jar,postgresql-8.3-604.jdbc3.jar,sqlitejdbc-v053-pure.jar,core.jar'
		width='1500' height='800'>
		<param name="score" value="${score}">
		<param name="topic" value="${currentTopic}">
	</applet>

</body>
</html>
