<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<%@tag description="Generic login template" pageEncoding="UTF-8" %>
<%@attribute name="bgColor" fragment="true" %>
<%@attribute name="message" fragment="true" %>
<%@attribute name="action" fragment="true" required="true" %>
<%@attribute name="handleValue" fragment="true" %>
<%@attribute name="passwordValue" fragment="true" %>
<div class="container <jsp:invoke fragment="bgColor"/>">
    <script>
        Materialize.toast('<jsp:invoke fragment="message"/>', 3000);
    </script>
    <div class="row valign-wrapper" style="height: 100vh; margin: 0;">
        <div class="col l4 m4 s4"></div>
        <div class="col l4 m4 s4 card">
            <div class="card-content">
                <p class="flow-text">Login</p>
                <form action="<jsp:invoke fragment="action"/>" method="post">
                    <div class="input-field">
                        <i class="material-icons prefix">account_circle</i>
                        <input id="icon_prefix" name="handle"
                               value="<jsp:invoke fragment="handleValue"/>"
                               type="text" maxlength="20">
                        <label for="icon_prefix">Handle</label>
                    </div>
                    <div class="input-field">
                        <i class="material-icons prefix">fingerprint</i>
                        <input id="icon_telephone" name="password"
                               value="<jsp:invoke fragment="passwordValue"/>"
                               type="password" maxlength="20">
                        <label for="icon_telephone">Password</label>
                    </div>
                    <div class="input-field right-align">
                        <button class="btn-flat black-text" type="submit"><i class="material-icons">send</i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
        <div class="col l4 m4 s4"></div>
    </div>
</div>
