<%@tag description="Basic template with javascript headers" pageEncoding="UTF-8" %>
<%@attribute name="title" fragment="true" %>
<%@attribute name="extraHeaders" fragment="true" %>
<%@attribute name="css" fragment="true" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="${pageContext.request.contextPath}/static/images/logo.png">
    <%--JQuery--%>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery/jquery-3.2.1.min.js"></script>
    <%--Materialize--%>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/css/materialize.min.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <script src="${pageContext.request.contextPath}/static/materialize/materialize.min.js"></script>
    <%--React--%>
    <script src="${pageContext.request.contextPath}/static/reactjs/react.js"></script>
    <script src="${pageContext.request.contextPath}/static/reactjs/react-dom.js"></script>
    <script src="${pageContext.request.contextPath}/static/reactjs/babel.js"></script>
    <%--Extra Headers--%>
    <jsp:invoke fragment="extraHeaders"/>
    <title>
        <jsp:invoke fragment="title"/>
    </title>
    <jsp:invoke fragment="css"/>
</head>
<body>
<jsp:doBody/>
</body>
</html>
