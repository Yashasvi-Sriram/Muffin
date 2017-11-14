<%@tag description="Basic template with javascript headers" pageEncoding="UTF-8" %>
<%@attribute name="contextPath" fragment="true" required="true" %>
<%@attribute name="inSessionMovieOwnerId" fragment="true" required="true" %>

<link href="https://fonts.googleapis.com/css?family=Lobster" rel="stylesheet">
<script type="text/javascript">
    $(document).ready(function () {
        $('.button-collapse').sideNav({
            menuWidth: '50vw',
            edge: 'right',
            closeOnClick: true,
            draggable: true,
        });

        $('.modal').modal();

        $('.parallax').parallax();
    });
</script>
<%--Home Shortcut--%>
<a href="<jsp:invoke fragment="contextPath"/>/movieowner/home"
   title="Home"
   class="btn-floating btn-large waves-effect waves-light red"
   style="position:fixed;bottom: 100px; right: 20px">
    <i class="material-icons">home</i>
</a>

<%--Nav bar--%>
<ul id="nav-bar" class="side-nav">
    <li>
        <div class="user-view brown" style="margin: 0">
            <img src="<jsp:invoke fragment="contextPath"/>/static/images/logo.png" alt="Muffi(co)n">
            <span class="white-text"
                  style="font-size: 50px; font-family: 'Lobster', cursive;">
                M<span class="grey-text">ovie</span>
                <span class="grey-text">B</span>uff
                in<span class="grey-text">c.</span></span>
        </div>
    </li>
    <li>
        <div class="divider" style="margin: 0"></div>
    </li>
    <li>
        <a href="<jsp:invoke fragment="contextPath"/>/movieowner/home"
           title="Home">
            <i class="material-icons">home</i>
            Home
        </a>
    </li>
    <li>
        <a href="<jsp:invoke fragment="contextPath"/>/movieowner/movieeditor"
           title="Movie Editor">
            <i class="material-icons">edit</i>
            Movie Editor
        </a>
    </li>
    <li>
        <a href="<jsp:invoke fragment="contextPath"/>/movieowner/logout"
           title="Logout">
            <i class="material-icons">power_settings_new</i>
            Log out
        </a>
    </li>
</ul>
<%--Nav bar trigger--%>
<a href="#"
   data-activates="nav-bar"
   class="btn-floating btn-large waves-effect waves-light brown button-collapse pulse"
   style="position:fixed;bottom: 20px; right: 20px">
    <i class="material-icons">menu</i>
</a>

<div class="parallax-container">
    <div class="parallax"><img src="${pageContext.request.contextPath}/static/images/sky.jpg"
                               alt="Muffin"/></div>
</div>
<jsp:doBody/>