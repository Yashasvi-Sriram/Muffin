<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name}  | Home</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            .muff-search-app {
                position: fixed;
                top: 0;
                right: 0;
                width: 25vw;
            }

            .muff-search-app td {
                padding: 10px;
                font-size: large;
            }

            .muff-search-app input {
                margin: 0 !important;
                font-size: large !important;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <script type="text/babel">
            let MuffSearchResult = React.createClass({
                render: function () {
                    return (
                            <tr>
                                <td>{this.props.name}</td>
                                <td className="pink-text">{this.props.handle}</td>
                            </tr>
                    );
                }
            });

            let MuffSearchApp = React.createClass({
                getInitialState: function () {
                    return {
                        muffs: [],
                    }
                },
                onRegexInputKeyDown: function (e) {
                    let self = this;
                    switch (e.keyCode || e.which) {
                        // Enter Key
                        case 13:
                            let self = this;
                            $.ajax({
                                url: '${pageContext.request.contextPath}/muff/search',
                                type: 'GET',
                                data: {searchKey: e.target.value},
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        self.setState(ps => {
                                            return {muffs: data};
                                        });
                                        $(self.refs.results).show();
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                            break;
                        // Escape key
                        case 27:
                            $(this.refs.results).hide();
                            break;
                        default:
                            break;
                    }
                },
                render: function () {
                    let results = this.state.muffs.map(muff => {
                        return <MuffSearchResult
                                key={muff.id}
                                id={muff.id}
                                name={muff.name}
                                handle={muff.handle}
                                level={muff.level}
                        />;
                    });
                    return (
                            <div className="muff-search-app">
                                <input onKeyDown={this.onRegexInputKeyDown} placeholder="Search" type="text"/>
                                <table ref="results" className="striped highlight">
                                    <tbody>
                                    {results.length === 0 ? <tr><td className="red white-text">No matching muffs</td></tr> : results}
                                    </tbody>
                                </table>
                            </div>
                    );
                },
                componentDidMount: function () {
                    $(this.refs.results).hide();
                },
            });

            ReactDOM.render(<MuffSearchApp/>, document.getElementById('muff-search-app'));
        </script>
        <div class="container">
            <div id="muff-search-app" style=""></div>
            Hello, ${sessionScope.get(SessionKeys.MUFF).name}
        </div>
    </jsp:body>
</m:base>