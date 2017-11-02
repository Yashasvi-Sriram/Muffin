<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            input {
                border: none!important;
                box-shadow: none!important;
                outline: none!important;
                color: black!important;
                font-size: x-large!important;
                margin-left: 5%!important;
                width: 80%!important;
                margin-right: 5%!important;
                margin-bottom: 0!important;
            }
            input:focus {
                border: none!important;
                box-shadow: none!important;
                outline: 1px solid black!important;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <script type="text/babel">
            let truncate = function (string, maxLength) {
                let label;
                if (string.length > maxLength) {
                    label = string.substring(0, maxLength) + '...';
                }
                else {
                    label = string;
                }
                return label;
            };
            const DUR_MAX = 500;
            let isMovieValid = function (name, durationInMinutes) {
                if (name === '') {
                    Materialize.toast('Movie name is empty!', 3000);
                    return false;
                }
                if (name.length > 50) {
                    Materialize.toast('Movie name must be less than 50 characters!', 3000);
                    return false;
                }

                if (durationInMinutes === ''
                    || isNaN(Number(durationInMinutes))) {
                    Materialize.toast('Invalid duration!', 3000);
                    return false;
                }
                durationInMinutes = Number(durationInMinutes);
                if (durationInMinutes <= 0 || durationInMinutes > DUR_MAX) {
                    Materialize.toast('Invalid duration!', 3000);
                    return false;
                }
                return true;
            };

            /**
             * @propFunctions: onDeleteClick, onEditClick
             * */
            let CharacterItem = React.createClass({
                getInitialState: function () {
                    return {
                        inReadMode: true,
                    }
                },
                readModeRender: function () {
                    return (
                            <tr title={this.props.name}>
                                <td className="flow-text">{truncate(this.props.name, 25)}</td>
                                <td className="flow-text">{truncate(this.props.actorName, 25)}</td>
                            </tr>
                    );
                },
                writeModeRender: function () {
                    return (
                            <tr title={this.props.name}
                                onDoubleClick={() =>
                                    this.setState(() => {
                                        return {inReadMode: true}
                                    })
                                }>
                                <td className="flow-text">
                                    <input type="text"
                                           ref="name"
                                           name="name"
                                           placeholder="Name"
                                           defaultValue={this.props.name}/>
                                </td>
                                <td>
                                    <input type="number"
                                           ref="durationInMinutes"
                                           name="durationInMinutes"
                                           placeholder="Duration (In Minutes)"
                                           defaultValue={this.props.durationInMinutes}/>
                                </td>
                                <td>
                                    <a href="#"
                                       onClick={(e) => {
                                           this.props.onEditClick(this.props.id, this.refs.name.value, this.refs.durationInMinutes.value);
                                           this.setState(() => {
                                               return {inReadMode: true}
                                           });
                                       }}
                                       className="btn-floating waves-effect waves-light yellow darken-4">
                                        <i className="material-icons">send</i>
                                    </a>
                                </td>
                                <td>
                                    <a href="#"
                                       onClick={(e) => {
                                           this.setState(() => {
                                               return {inReadMode: true}
                                           })
                                       }}
                                       className="btn-floating waves-effect waves-light black">
                                        <i className="material-icons">cancel</i>
                                    </a>
                                </td>
                            </tr>
                    );
                },
                render: function () {
                    return  this.readModeRender();
                }
            });

            let CharacterEditor = React.createClass({
                getInitialState: function () {
                    return {
                        characters: [
                            <jstl:forEach items="${requestScope.characterList}" var="character">
                            {
                                id: ${character.id},
                                name: '${character.name}',
                                movieId: '${character.movieId}',
                                actorId: '${character.actorId}',
								actorName: '${character.actorName}',
                            },
                            </jstl:forEach>
                        ],
                    }
                },
                createMovie: function () {
                    let self = this;
                    // validation
                    if (!isMovieValid(this.refs.name.value, this.refs.durationInMinutes.value)) {
                        return;
                    }
                    // ajax call
                    let newMovieSerialized = $(this.refs.createMovieForm).find('input').serialize();
                    $.ajax({
                        url: '${pageContext.request.contextPath}/movieowner/movieeditor/create',
                        type: 'GET',
                        data: newMovieSerialized,
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                let data = json.data;
                                self.setState((prevState, props) => {
                                    prevState.movies.push(data);
                                    return prevState;
                                });
                                $(self.refs.createMovieForm).find('input').val('');
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                editMovie: function (id, name, durationInMinutes) {
                    let self = this;
                    // validation
                    if (!isMovieValid(name, durationInMinutes)) {
                        return;
                    }
                    // ajax call
                    $.ajax({
                        url: '${pageContext.request.contextPath}/movieowner/movieeditor/update',
                        type: 'GET',
                        data: {id: id, name: name, durationInMinutes: durationInMinutes},
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                let data = json.data;
                                self.setState((prevState, props) => {
                                    let updateIndex = -1;
                                    prevState.movies.forEach((movie, i) => {
                                        if (movie.id === id) {
                                            updateIndex = i;
                                        }
                                    });
                                    prevState.movies.splice(updateIndex, 1, data);
                                    return prevState;
                                });
                                self.forceUpdate();
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                deleteMovie: function (id) {
                    let self = this;
                    $.ajax({
                        url: '${pageContext.request.contextPath}/movieowner/movieeditor/delete',
                        type: 'GET',
                        data: {id: id},
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                self.setState((prevState, props) => {
                                    let delIndex = -1;
                                    prevState.movies.forEach((movie, i) => {
                                        if (movie.id === id) {
                                            delIndex = i;
                                        }
                                    });
                                    prevState.movies.splice(delIndex, 1);
                                    return prevState;
                                });
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },

				infoMovie: function (id) {
                    let self = this;
					console.log('${pageContext.request.contextPath}/movieowner/movieinfo');
                    $.ajax({
                        url: '${pageContext.request.contextPath}/movieowner/movieinfo',
                        type: 'POST',
                        data: {id: id},
						success: function (r) {
                           		console.log(r);
                            },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                render: function () {
                    return (
                            <table className="highlight centered striped">
                                <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Actor Name</th>
                                </tr>
                                </thead>
                                <tbody>
                                {
                                    this.state.characters.map(m => {
                                        return <CharacterItem key={m.id}
                                                          id={m.id}
                                                          name={m.name}
                                                          actorName={m.actorName}
                                                          />;
                                    })
                                }
                                </tbody>
                            </table>
                    );
                }
            });

            ReactDOM.render(<CharacterEditor/>, document.getElementById('app'));
        </script>
        <div id="app" class="container"></div>
    </jsp:body>
</m:base>