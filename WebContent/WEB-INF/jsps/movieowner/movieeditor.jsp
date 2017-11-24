<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            input {
                border: none !important;
                box-shadow: none !important;
                outline: none !important;
                color: black !important;
                font-size: x-large !important;
                margin-left: 5% !important;
                width: 80% !important;
                margin-right: 5% !important;
                margin-bottom: 0 !important;
            }

            input:focus {
                border: none !important;
                box-shadow: none !important;
                outline: 1px solid black !important;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <m:insessionmovieownercommons>
            <jsp:attribute
                    name="inSessionMovieOwnerId">${sessionScope.get(SessionKeys.MOVIE_OWNER).getId()}</jsp:attribute>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
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
                    let MovieItem = React.createClass({
                        getInitialState: function () {

                            return {
                                inReadMode: true,
                            }
                        },
                        readModeRender: function () {
                            let url = "${pageContext.request.contextPath}/movieowner/moviedetail?movieId=" + this.props.id;

                            return (
                                    <tr title={this.props.name}
                                        onDoubleClick={() => this.setState(() => {
                                            return {inReadMode: false}
                                        })}>
                                        <td>{truncate(this.props.name, 20)}</td>
                                        <td>{this.props.durationInMinutes}</td>

                                        <td title="info">
                                            <a href={url} type="submit" name="movieId" value={this.props.id}
                                               className="btn-floating waves-effect waves-light blue">
                                                <i className="material-icons">info</i></a>
                                        </td>
                                        <td title="remove">
                                            <a
                                                    onClick={(e) => {
                                                        this.props.onDeleteClick(this.props.id)
                                                    }}
                                                    className="btn-floating waves-effect waves-light red">
                                                <i className="material-icons">remove</i>
                                            </a>
                                        </td>
                                        <td title="edit">
                                            <a
                                                    onClick={(e) => {
                                                        this.setState(()
                                                    =>
                                                        {
                                                            return {inReadMode: false}
                                                        }
                                                    )
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
                                        </td>
                                        <td title="send">
                                            <a
                                                    onClick={(e) => {
                                                        this.props.onEditClick(this.props.id, this.refs.name.value, this.refs.durationInMinutes.value);
                                                        this.setState(()
                                                    =>
                                                        {
                                                            return {inReadMode: true}
                                                        }
                                                    )
                                                        ;
                                                    }}
                                                    className="btn-floating waves-effect waves-light yellow darken-4">
                                                <i className="material-icons">send</i>
                                            </a>
                                        </td>
                                        <td title="cancel">
                                            <a
                                                    onClick={(e) => {
                                                        this.setState(()
                                                    =>
                                                        {
                                                            return {inReadMode: true}
                                                        }
                                                    )
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
                                movieSearchResults: [],
                                offset: 0,
                            }
                        },
                        getDefaultProps: function () {
                            return {

                                limit: 5,
                                movieSearchUrl: '/movie/movieownersearch',
                            }
                        },
                        _resetOffset: function () {
                            this.state.offset = 0;
                        },
                        _incrementOffset: function (fetchedDataLength) {
                            // last batch
                            if (fetchedDataLength < this.props.limit) {
                                this.state.offset -= this.props.limit;
                                this.state.offset += fetchedDataLength;
                            }
                            // update limit
                            this.state.offset += this.props.limit;
                        },
                        _decrementOffset: function () {
                            let floorExcess = this.state.offset % this.props.limit;
                            this.state.offset -= floorExcess;
                            if (floorExcess === 0) {
                                let prevOffset = this.state.offset - 2 * this.props.limit;
                                this.state.offset = prevOffset < 0 ? 0 : prevOffset;
                            } else {
                                let prevOffset = this.state.offset - this.props.limit;
                                this.state.offset = prevOffset < 0 ? 0 : prevOffset;
                            }
                        },
                        fetchNextBatch: function (pattern) {
                            let url = '${pageContext.request.contextPath}/movie/searchbymovieowner';
                            let self = this;
                            $.ajax({
                                url: url,
                                type: 'GET',
                                data: {pattern: pattern, offset: self.state.offset, limit: self.props.limit},
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        self._incrementOffset(data.length);
                                        // no movieSearchResults
                                        if (data.length === 0) {
                                            Materialize.toast('End of search!', 2000);
                                            return;
                                        }
                                        // add movieSearchResults
                                        self.setState(ps => {
                                            return {movieSearchResults: data};
                                        });
                                        $(self.refs.movieSearchResults).show();
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                        },
                        fetchPreviousBatch: function (pattern) {
                            this._decrementOffset();
                            if (this.state.offset === 0) {
                                Materialize.toast('Start of search!', 2000);
                            }
                            this.fetchNextBatch(pattern);
                        },
                        onRegexInputKeyDown: function (e) {
                            switch (e.keyCode || e.which) {
                                // Enter Key
                                case 13:
                                    let self = this;
                                    this._resetOffset();
                                    self.setState({movieSearchResults: []});
                                    this.fetchNextBatch(e.target.value);
                                    break;
                                // Escape key
                                case 27:
                                    $(this.refs.movieSearchResults).hide();
                                    break;
                                default:
                                    break;
                            }
                        },

                        onSearchClick: function (pattern) {
                            console.log("hey");
                            let self = this;
                            this._resetOffset();
                            self.setState({movieSearchResults: []});
                            this.fetchNextBatch(pattern);

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
                                url: '${pageContext.request.contextPath}/movie/create',
                                type: 'GET',
                                data: newMovieSerialized,
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {

                                        Materialize.toast('Added Succesfully', 2000);

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
                                url: '${pageContext.request.contextPath}/movie/update',
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
                                            prevState.movieSearchResults.forEach((movie, i) => {
                                                if (movie.id === id) {
                                                    updateIndex = i;
                                                }
                                            });
                                            prevState.movieSearchResults.splice(updateIndex, 1, data);
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
                                url: '${pageContext.request.contextPath}/movie/delete',
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
                                            prevState.movieSearchResults.forEach((movie, i) => {
                                                if (movie.id === id) {
                                                    delIndex = i;
                                                }
                                            });
                                            prevState.movieSearchResults.splice(delIndex, 1);
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
                            let movieSearchResults = this.state.movieSearchResults.map(m => {

                                return <MovieItem key={m.id}
                                                  id={m.id}
                                                  name={m.name}
                                                  durationInMinutes={m.durationInMinutes}
                                                  genres={m.genres}
                                                  onDeleteClick={this.deleteMovie}
                                                  onEditClick={this.editMovie}/>;

                            });
                            return (
                                    <div>
                                        <table className="highlight centered striped">
                                            <thead>
                                            <tr>
                                                <th><h5>Name</h5></th>
                                                <th><h5>Duration (In minutes)</h5></th>
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
                                                </td>
                                                <td>
                                                    <button onClick={this.createMovie}
                                                            className="btn-floating waves-effect waves-light green">
                                                        <i className="material-icons">add</i>
                                                    </button>
                                                </td>
                                            </tr>

                                            </tbody>
                                        </table>
                                        <div className="input-field">
                                            <input type="text" ref="pattern" placeholder="Search For Your Movies"
                                                   onKeyDown={this.onRegexInputKeyDown}
                                                   defaultValue=""/>
                                            <span className="right">
                                                    <button className="btn btn-flat"
                                                            onClick={e => this.onSearchClick(this.refs.pattern.value)}><i
                                                            className="material-icons">search</i></button>
                                            </span>
                                        </div>
                                        <div ref="movieSearchResults">
                                            <div>
                                                <div className="collection with-header">
                                                    <div className="collection-header"><span className="flow-text">Movies</span>
                                                        <span className="right">
                                                        <button className="btn btn-flat"
                                                                onClick={e => this.onRegexInputKeyDown}><i
                                                                className="material-icons">search</i></button>
                                                        <button className="btn btn-flat"
                                                                onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                                                className="material-icons">keyboard_arrow_left</i></button>
                                                        <button className="btn btn-flat"
                                                                onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                                                className="material-icons">keyboard_arrow_right</i></button>
                                                        </span>
                                                    </div>

                                                </div>
                                            </div>
                                            <table className="highlight centered striped">
                                                <thead>
                                                <tr>
                                                    <th><h5>Name</h5></th>
                                                    <th><h5>Duration (In minutes)</h5></th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                {movieSearchResults}
                                                </tbody>
                                            </table>
                                        </div>

                                    </div>

                            );
                        },
                        componentDidMount: function () {
                            $(this.refs.movieSearchResults).hide();
                        },
                    });

                    ReactDOM.render(<MovieEditor/>, document.getElementById('app'));
                </script>
                <div class="container" style="min-height: 100vh">
                    <h2>${sessionScope.get(SessionKeys.MOVIE_OWNER).getName()}'s movies</h2>
                    <div id="app"></div>
                </div>
            </jsp:body>
        </m:insessionmovieownercommons>
    </jsp:body>
</m:base>