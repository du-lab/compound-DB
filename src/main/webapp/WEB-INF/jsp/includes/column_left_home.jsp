<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside>
    <nav>
        <ul>
            <li><a href="<c:url value="/" />">Home</a></li>
            <li><a href="<c:url value="/submission"/>">New Submission</a></li>
            <li><a href="<c:url value="/library" />">Library</a></li>
            <c:if test="${userPrincipal == null}">
                <li><a href="<c:url value="/login" />">Log-in / Sign-up</a></li>
            </c:if>
            <c:if test="${userPrincipal != null}">
                <li><a href="<c:url value="/logout" />">Log out</a></li>
            </c:if>
        </ul>
    </nav>
</aside>

<article>