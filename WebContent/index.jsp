<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">
      Index
    </jsp:attribute>
    <jsp:body>
        <script>
            $(document).ready(function () {
                $('.parallax').parallax();
            });
        </script>
        <div class="parallax-container">
            <div class="parallax"><img src="${pageContext.request.contextPath}/static/penguin.jpg"/></div>
        </div>
        <div class="container" style="height: 50vh">
            <div class="section flow-text white">
                Welcome to
                <span class="pink-text">M</span>ovie
                B<span class="pink-text">uff</span>
                <span class="pink-text">In</span>c.
                <img src="${pageContext.request.contextPath}/static/logo.ico" alt="Muffin">
            </div>
            <div class="collection">
                <a href="${pageContext.request.contextPath}/muff/login" class="collection-item">
                    Login as Muff
                    <span class="secondary-content"><i class="material-icons">fingerprint</i></span>
                </a>
                <a href="${pageContext.request.contextPath}/muff/signup" class="collection-item">
                    Signup as Muff
                    <span class="secondary-content"><i class="material-icons">face</i></span>
                </a>
                <a href="${pageContext.request.contextPath}/movieowner/login" class="collection-item">
                    Login as Movie Owner
                    <span class="secondary-content"><i class="material-icons">local_movies</i></span>
                </a>
                <a href="${pageContext.request.contextPath}/cinemabuildingowner/login" class="collection-item">
                    Login as Cinema Building Owner
                    <span class="secondary-content"><i class="material-icons">account_balance</i></span>
                </a>
            </div>
        </div>
        <div class="parallax-container">
            <div class="parallax"><img src="${pageContext.request.contextPath}/static/fireways.jpg"/></div>
        </div>
    </jsp:body>
</m:base>