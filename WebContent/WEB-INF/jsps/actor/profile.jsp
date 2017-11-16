<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name} | Movie Details</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            #infinite-feed-app {
                margin-bottom: 50vh;
            }

            #infinite-feed-app .review {
                background-color: whitesmoke;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <m:insessionmuffcommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute name="inSessionMuffId">${sessionScope.get(SessionKeys.MUFF).getId()}</jsp:attribute>
            <jsp:body>
                <script type="text/babel"
                        src="${pageContext.request.contextPath}/static/muffinjs/togglelikesapp.jsx"></script>
                <script type="text/babel">
                    ReactDOM.render(<ToggleLikesApp
                            muffId={${sessionScope.get(SessionKeys.MUFF).getId()}}
                            actorId={${requestScope.profileActor.getId()}}
                            actorName={'${requestScope.profileActor.getName()}'}
                            contextPath="${pageContext.request.contextPath}"
                            toggleLikesUrl='/actor/likes/toggle'
                            countLikesUrl='/actor/likes/count'
                            doesLikesUrl='/actor/likes/does'/>, document.getElementById('toggle-likes-app'));
                </script>
                <script src="${pageContext.request.contextPath}/static/chartjs/Chart.min.js"></script>
                <%--Infinite Feed App--%>
                <div class="row" style="min-height: 100vh">
                    <div class="col s4">
                        <div class="collection with-header">
                            <div class="collection-header"><h4>${requestScope.profileActor.getName()}</h4></div>
                            <div class="collection-item">Movies Acted
                                <div class="secondary-content">${requestScope.movieMap.size()}
                                </div>
                            </div>
                            <div class="collection-item">Genre distribution</div>
                            <canvas id="myChart" width="400" height="400"></canvas>
                            <script>
                                let ctx = document.getElementById("myChart").getContext('2d');
                                let myChart = new Chart(ctx, {
                                    type: 'bar',
                                    data: {
                                        labels: [
                                            <jstl:forEach items="${requestScope.genreHistogram}" var="bin">
                                            "${bin.key.getName()}",
                                            </jstl:forEach>
                                        ],
                                        datasets: [{
                                            label: 'Number of movies',
                                            data: [
                                                <jstl:forEach items="${requestScope.genreHistogram}" var="bin">
                                                ${bin.value},
                                                </jstl:forEach>
                                            ],
                                            backgroundColor: 'rgba(255, 99, 132, 0.7)',
                                            borderColor: 'rgba(255,99,132,1)',
                                            borderWidth: 1,
                                        }]
                                    },
                                    options: {
                                        scales: {
                                            yAxes: [{
                                                ticks: {
                                                    beginAtZero: true,
                                                    stepSize: 1,
                                                }
                                            }]
                                        }
                                    }
                                });
                            </script>
                        </div>
                    </div>
                    <div class="col s4">
                        <div class="collection with-header">
                            <div class="collection-header"><h4>Movies List</h4></div>
                            <jstl:forEach items="${requestScope.movieMap}" var="movie">
                                <div class="collection-item">
                                    <a href="${pageContext.request.contextPath}/movie/profile?movieId=${movie.key}"
                                       class="purple-text">
                                        <div>${movie.value}</div>
                                    </a>
                                </div>
                            </jstl:forEach>
                        </div>

                    </div>
                    <div class="col s4">
                        <div id="toggle-likes-app"></div>
                    </div>
                </div>
            </jsp:body>
        </m:insessionmuffcommons>
    </jsp:body>
</m:base>