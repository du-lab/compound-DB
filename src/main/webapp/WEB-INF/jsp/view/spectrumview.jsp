<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/includes/header.jsp"/>
<jsp:include page="/includes/column_left_home.jsp"/>

<!-- Start the middle column -->

<section>
    <h1>Spectrum</h1>
    <h2>${name}</h2>
    <br/>
    <h2>Properties</h2>
    <table>
        <tr>
            <th>Name</th>
            <th>Value</th>
        </tr>
        <c:forEach var="e" items="${properties}">
            <tr>
                <td>${e.key}</td>
                <td>${e.value}</td>
            </tr>
        </c:forEach>
    </table>
    <br/>
    <h2>Spectrum</h2>
    <div id="chartDiv"></div>
</section>

<script>
    var chartData = {
        type: 'bar',
        title: {text: 'My First Chart'},
        legend: {},
        series: [
            {values: JSON.parse('${jsonPeaks}')}
        ],
        plot: {
            tooltip: {
                text: 'm/z: %kt<br>int: %vt'
            }
        }
    };
    zingchart.render({
        id: 'chartDiv',
        data: chartData,
        height: 400,
        width: 600
    });
</script>

<!-- End the middle column -->

<jsp:include page="/includes/column_right_news.jsp"/>
<jsp:include page="/includes/footer.jsp"/>