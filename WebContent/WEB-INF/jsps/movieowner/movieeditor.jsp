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
                    Materialize.toast('Movie name is empty!', 1000);
                    return false;
                }
                if (name.length > 50) {
                    Materialize.toast('Movie name must be less than 50 characters!', 1000);
                    return false;
                }

                if (durationInMinutes === ''
                    || isNaN(Number(durationInMinutes))) {
                    Materialize.toast('Invalid duration!', 1000);
                    return false;
                }
                durationInMinutes = Number(durationInMinutes);
                if (durationInMinutes <= 0 || durationInMinutes > DUR_MAX) {
                    Materialize.toast('Invalid duration!', 1000);
                    return false;
                }
                return true;
            };

            /**
             * @propFunctions: onDeleteClick, onEditClick
             * */
            let MovieItem = React.createClass({
                getInitialState: function () {
                    return {
                        inReadMode: true,
                    }
                },
                readModeRender: function () {
                    return (
                            <tr title={this.props.name}
                                onDoubleClick={() => {
                                    this.setState(() => {
                                        return {inReadMode: false}
                                    })
                                }}>
                                <td className="flow-text">{truncate(this.props.name, 20)}</td>
                                <td>{this.props.durationInMinutes}</td>
                                <td>
                                    <a href="#"
                                       onClick={(e) => {
                                           this.props.onDeleteClick(this.props.id)
                                       }}
                                       className="btn-floating waves-effect waves-light red">
                                        <i className="material-icons">remove</i>
                                    </a>
                                </td>
                                <td>
                                    <a href="#"
                                       onClick={(e) => {
                                           this.setState(() => {
                                               return {inReadMode: false}
                                           })
                                       }}
                                       className="btn-floating waves-effect waves-light yellow darken-4">
                                        <i className="material-icons">edit</i>
                                    </a>
                                </td>
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
                    return this.state.inReadMode ? this.readModeRender() : this.writeModeRender();
                }
            });

            let MovieEditor = React.createClass({
                getInitialState: function () {
                    return {
                        movies: [
                            <jstl:forEach items="${requestScope.movieList}" var="movie">
                            {
                                id: ${movie.id},
                                name: '${movie.name}',
                                movieOwnerId: '${movie.movieOwnerId}',
                                durationInMinutes: ${movie.durationInMinutes},
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
                render: function () {
                    return (
                            <table className="highlight centered striped">
                                <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Duration (In minutes)</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr className="create-movie-form"
                                    ref="createMovieForm">
                                    <td>
                                        <input type="text"
                                               ref="name"
                                               name="name"
                                               placeholder="Name"
                                               defaultValue=""/>
                                    </td>
                                    <td>
                                        <input type="number"
                                               ref="durationInMinutes"
                                               name="durationInMinutes"
                                               placeholder="Duration (In Minutes)"
                                               defaultValue=""/>
                                    </td>
                                    <td>
                                    </td>
                                    <td>
                                        <button onClick={this.createMovie}
                                                className="btn-floating waves-effect waves-light green">
                                            <i className="material-icons">add</i>
                                        </button>
                                    </td>
                                </tr>
                                {
                                    this.state.movies.map(m => {
                                        return <MovieItem key={m.id}
                                                          id={m.id}
                                                          name={m.name}
                                                          durationInMinutes={m.durationInMinutes}
                                                          onDeleteClick={this.deleteMovie}
                                                          onEditClick={this.editMovie}/>;
                                    })
                                }
                                </tbody>
                            </table>
                    );
                }
            });

            ReactDOM.render(<MovieEditor/>, document.getElementById('app'));
        </script>
        <div id="app" class="container"></div>
    </jsp:body>
</m:base>