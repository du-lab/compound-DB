<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>ADAP Compound Library</title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/resources/css/datatables.min.css"/>">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>
<body>

<header>
    <h1>ADAP Compound Library</h1>
    <h2>Some information</h2>

    <c:if test="${userPrincipal != null}">
        <div class="user">User: ${userPrincipal} (<a href="<c:url value="/logout"/>">Log out</a>)</div>
    </c:if>
</header>