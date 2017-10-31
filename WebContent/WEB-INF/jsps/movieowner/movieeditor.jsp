<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            .create-movie-form input.validate {
                margin: 0;
                color: black;
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

            /**
             * @propFunctions:
             * */
            let MovieItem = React.createClass({
                render: function () {
                    return (
                            <tr title={this.props.name}>
                                <td className="flow-text">{truncate(this.props.name, 20)}</td>
                                <td>{this.props.durationInMinutes}</td>
                                <td>
                                    <a href="#"
                                       className="btn-floating waves-effect waves-light red">
                                        <i className="material-icons">remove</i>
                                    </a>
                                </td>
                            </tr>
                    );
                }
            });

            let MovieEditor = React.createClass({
                getDefaultProps: function () {
                    return {
                        DUR_MAX: 500,
                    }
                },
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
                    if (this.refs.name.value === '') {
                        Materialize.toast('Movie name is empty!', 1000);
                        return;
                    }
                    let duration = this.refs.durationInMinutes.value;
                    if (duration === ''
                        || isNaN(Number(duration))) {
                        Materialize.toast('Invalid duration!', 1000);
                        return;
                    }
                    duration = Number(duration);
                    if (duration <= 0 || duration > this.props.DUR_MAX) {
                        Materialize.toast('Invalid duration!', 1000);
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
                            } else {
                                let data = json.data;
                                self.setState((prevState, props) => {
                                    prevState.movies.push(data);
                                    return {movies: prevState.movies};
                                });
                                $(self.refs.createMovieForm).find('input').val('');
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
                                    <td className="flow-text">
                                        <input className="validate"
                                               type="text"
                                               ref="name"
                                               name="name"
                                               placeholder="Name"
                                               defaultValue=""/>
                                    </td>
                                    <td>
                                        <input className="validate"
                                               type="number"
                                               ref="durationInMinutes"
                                               name="durationInMinutes"
                                               placeholder="Duration (in Minutes)"
                                               defaultValue=""/>
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
                                                          durationInMinutes={m.durationInMinutes}/>;
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