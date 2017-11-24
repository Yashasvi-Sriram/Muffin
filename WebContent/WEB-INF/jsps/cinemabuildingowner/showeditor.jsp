<%@page import="org.muffin.muffin.servlets.SessionKeys" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute
            name="title">Screen No. ${requestScope.theatre.getScreenNo()} </jsp:attribute>
    <jsp:body>
        <m:insessioncinemabuildingownercommons>
            <jsp:attribute name="contextPath">${pageContext.request.contextPath}</jsp:attribute>
            <jsp:attribute
                    name="inSessionCinemaBuildingOwnerId">${sessionScope.get(SessionKeys.CINEMA_BUILDING_OWNER).getId()}</jsp:attribute>
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

                    let isMovieValid = function (movieName) {
                        if (movieName === '') {
                            Materialize.toast('Movie Name is Empty', 2000);
                            return false;
                        }
                        return true;
                    };

                    let isTimeValid = function (dateTime) {
                        if (dateTime) {
                            return true;
                        }
                        else {
                            Materialize.toast('Invalid Start/End Time', 2000);
                            return false;
                        }
                    };

                    let dateTimeString = function (localDateTime) {
                        let p = moment();
                        p.year(localDateTime.date.year).month(localDateTime.date.month - 1).date(localDateTime.date.day);
                        p.hour(localDateTime.time.hour).minute(localDateTime.time.minute).second(localDateTime.time.second);
                        let str = p.format("YYYY-MM-DD");
                        let str2 = p.format("HH:mm");
                        return str + "T" + str2;
                    };

                    let dateString = function (localDateTimeString) {
                        let p = moment(localDateTimeString);
                        return p.format("Do MMM YY");

                    };

                    let timeString = function (localDateTimeString) {
                        let p = moment(localDateTimeString);
                        return p.format("HH:mm");

                    };

                    let ShowItem = React.createClass({
                        render: function () {
                            return (
                                    <tr title={this.props.movie.name}>
                                        <td>{truncate(this.props.movie.name, 25)}</td>
                                        <td>{dateString(this.props.showtime.startTime)}</td>
                                        <td>{timeString(this.props.showtime.startTime)}</td>
                                        <td>{dateString(this.props.showtime.endTime)}</td>
                                        <td>{timeString(this.props.showtime.endTime)}</td>
                                        <td>
                                            <a onClick={(e) => {
                                                this.props.onDeleteClick(this.props.id)
                                            }}
                                               className="btn-floating waves-effect waves-light red">
                                                <i className="material-icons">remove</i>
                                            </a>
                                        </td>
                                    </tr>
                            );
                        }
                    });


                    let MovieSearchResult = React.createClass({
                        render: function () {
                            return (
                                    <div className="collection-item"
                                         onClick={e => this.props.onItemClick(this.props.name)}>{this.props.name}</div>
                            );
                        }
                    });


                    let ShowEditor = React.createClass({
                        getInitialState: function () {
                            return {
                                movieSearchResults: [],
                                offset: 0,
                                theatreId: ${requestScope.theatre.getId()},
                                shows: [
                                    <jstl:forEach items="${requestScope.showList}" var="show">
                                    {
                                        id: ${show.id},
                                        theatreId: ${show.theatreId},
                                        movie: {name: '${show.movie.name}', id: ${show.movie.id}},
                                        showtime: {
                                            startTime: '${show.showtime.startTime}',
                                            endTime: '${show.showtime.endTime}'
                                        },
                                    },
                                    </jstl:forEach>
                                ],

                            }
                        },
                        getDefaultProps: function () {
                            return {
                                contextPath: '',
                                limit: 5,
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
                            let url = '${pageContext.request.contextPath}/movie/search';
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
                        selectMovie: function (text) {
                            this.refs.pattern.value = text;
                            $(this.refs.movieSearchResults).hide();

                        },
                        createShow: function () {
                            let self = this;
                            if (!isMovieValid(this.refs.pattern.value) || !isTimeValid(this.refs.startDateTime.value) || !isTimeValid(this.refs.endDateTime.value)) {
                                return;
                            }
                            // ajax call
                            $.ajax({
                                url: '${pageContext.request.contextPath}/show/create',
                                type: 'GET',
                                data: {
                                    theatreId: this.state.theatreId,
                                    startDateTime: this.refs.startDateTime.value,
                                    endDateTime: this.refs.endDateTime.value,
                                    movieName: this.refs.pattern.value
                                },
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        let putData = {
                                            id: data.id,
                                            theatreId: data.theatreId,
                                            movie: {name: data.movie.name, id: data.movie.id},
                                            showtime: {
                                                startTime: dateTimeString(data.showtime.startTime),
                                                endTime: dateTimeString(data.showtime.endTime)
                                            },
                                        };
                                        self.setState((prevState, props) => {
                                            prevState.shows.push(putData);
                                            return prevState;
                                        });

                                        $(self.refs.createShowForm).find('input').val('');
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                        },
                        deleteShow: function (id) {
                            let self = this;
                            $.ajax({
                                url: '${pageContext.request.contextPath}/show/delete',
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
                                            prevState.shows.forEach((show, i) => {
                                                if (show.id === id) {
                                                    delIndex = i;
                                                }
                                            });
                                            prevState.shows.splice(delIndex, 1);
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
                            let shows = this.state.shows.map(c => {
                                return <ShowItem key={c.id}
                                                 id={c.id}
                                                 movie={c.movie}
                                                 showtime={c.showtime}
                                                 onDeleteClick={this.deleteShow}
                                />;
                            });
                            let movieSearchResults = this.state.movieSearchResults.map(movie => {
                                return <MovieSearchResult
                                        key={movie.id}
                                        id={movie.id}
                                        name={movie.name}
                                        onItemClick={this.selectMovie}
                                />;
                            });
                            return (
                                    <div>
                                        <table className="highlight centered striped">
                                            <thead>
                                            <tr className="create-show-form"
                                                ref="createShowForm">

                                                <td>
                                                    <input onKeyDown={this.onRegexInputKeyDown}
                                                           ref="pattern"
                                                           placeholder="Movie" type="text"/>
                                                </td>
                                                <td>
                                                    <input type="datetime-local" name="startTime" ref="startDateTime"
                                                           placeholder="Show starts at (datetime)"/>
                                                </td>
                                                <td>
                                                    <input type="datetime-local" name="endTime" ref="endDateTime"
                                                           placeholder="Show ends at (datetime)"/>
                                                </td>
                                                <td>
                                                    <button onClick={this.createShow}
                                                            className="btn-floating waves-effect waves-light green">
                                                        <i className="material-icons">add</i>
                                                    </button>
                                                </td>
                                            </tr>
                                            </thead>
                                        </table>
                                        <div className="collection with-header" ref="movieSearchResults">
                                            <div className="collection-header"><span className="flow-text">Movies</span>
                                                <span className="right">
                                                <button className="btn btn-flat"
                                                        onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                                        className="material-icons">keyboard_arrow_left</i></button>
                                                <button className="btn btn-flat"
                                                        onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                                        className="material-icons">keyboard_arrow_right</i></button>
                                                </span>
                                            </div>
                                            {movieSearchResults}
                                        </div>
                                        <table className="highlight centered striped">
                                            <thead>
                                            <tr>
                                                <th>Movie Name</th>
                                                <th>Start Date</th>
                                                <th>Start Time</th>
                                                <th>End Date</th>
                                                <th>End Time</th>
                                            </tr>
                                            </thead>
                                            <tbody>{shows}</tbody>
                                        </table>
                                    </div>

                            );
                        },
                        componentDidMount: function () {
                            $(this.refs.movieSearchResults).hide();
                        },
                    });


                    ReactDOM.render(<ShowEditor/>, document.getElementById('app'));
                </script>
                <div class="container" style="min-height: 100vh">
                    <h1>Screen No. ${requestScope.theatre.getScreenNo()} </h1>
                    <div id="app"></div>
                </div>
            </jsp:body>
        </m:insessioncinemabuildingownercommons>
    </jsp:body>
</m:base>