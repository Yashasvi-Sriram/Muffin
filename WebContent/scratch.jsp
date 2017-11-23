<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">
      Scratch
    </jsp:attribute>
    <jsp:body>
        <div id="app"></div>
        <script type="text/babel"
                src="${pageContext.request.contextPath}/static/muffinjs/threatrecreaterapp.jsx"></script>
        <script type="text/babel">
            ReactDOM.render(<SeatingCreatorApp
            />, document.getElementById('app'));
        </script>
    </jsp:body>
</m:base>