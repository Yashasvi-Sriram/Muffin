<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">${sessionScope.get(SessionKeys.MUFF).name}  | Home</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            #top-action-bar {
                position: fixed;
                z-index: 1000;
                top: 0;
                left: 0;
                width: 100vw;
                height: 60px;
                background-color: #6d4c41;
            }

            #icon {
                position: absolute;
                left: 10px;
                top: 20px;
            }

            #muff-search-app {
                position: absolute;
                z-index: 1000;
                top: 10px;
                left: 25vw;
                width: 50vw;
                height: 40px;
                background-color: white;
                border-radius: 2px;
            }

            #muff-search-app td {
                padding: 10px;
                font-size: large;
                cursor: pointer;
            }

            #muff-search-app input {
                border: none !important;
                outline: none;
                box-shadow: none;
                margin: 0 !important;
                font-size: large !important;
            }

            #muff-search-app input:focus {
                border: none !important;
                outline: none;
                box-shadow: none !important;
            }

            #left-action-bar {
                position: fixed;
                z-index: 1000;
                top: 100px;
                left: 10px;
                width: 16vw;
                height: 85vh;
                border: 2px solid black;
            }

            #right-action-bar {
                position: fixed;
                z-index: 1000;
                top: 100px;
                right: 10px;
                width: 16vw;
                height: 85vh;
                border: 2px solid black;
            }

            #feed {
                position: relative;
                z-index: 999;
                top: 100px;
                border: 2px solid black;
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
                            <div>
                                <input onKeyDown={this.onRegexInputKeyDown}
                                       placeholder="Search" type="text"/>
                                <table className="white" ref="results">
                                    <tbody>
                                    {results.length === 0 ? <tr>
                                        <td className="red white-text">No matching muffs</td>
                                    </tr> : results}
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
        <div class="container-fluid">
            <div id="top-action-bar">
                <div id="icon"><img src="${pageContext.request.contextPath}/static/logo.ico"
                                    alt="muffin" width="64" height="64"></div>
                <div id="muff-search-app"></div>
            </div>
            <div class="row">
                <div id="left-action-bar">
                    <div class="truncate flow-text">${sessionScope.get(SessionKeys.MUFF).name}</div>
                </div>
                <div id="right-action-bar">
                    <div class="truncate flow-text">${sessionScope.get(SessionKeys.MUFF).name}</div>
                </div>
                <div class="col l2 m2 s2"></div>
                <div class="col l8 m8 s8">
                    <div id="feed" style="height: 200vh;">
                        <div>afddsafsas</div>
                        <div>afddsafsas</div>
                        <div>afddsafsas</div>
                        <div>afddsafsas</div>
                    </div>
                </div>
                <div class="col l2 m2 s2"></div>
            </div>
        </div>
    </jsp:body>
</m:base>