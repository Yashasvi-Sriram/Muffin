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

            .autocomplete-result-div {
                position: absolute;
                z-index: 1000;
                outline: 1px solid gray;
                background-color: white;
                cursor: pointer;
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

            let isCharacterValid = function (actorName, characterName) {
                if (actorName === '') {
                    Materialize.toast('Actor Name name is empty!', 3000);
                    return false;
                }
                if (actorName.length > 50) {
                    Materialize.toast('Actor name must be less than 50 characters!', 3000);
                    return false;
                }

                if (characterName === '') {
                    Materialize.toast('Character Name name is empty!', 3000);
                    return false;
                }
                if (characterName.length > 50) {
                    Materialize.toast('Character name must be less than 50 characters!', 3000);
                    return false;
                }

                return true;
            };

            let isGenreValid = function (genreName) {
                if (genreName === '') {
                    Materialize.toast('Genre Name name is empty!', 3000);
                    return false;
                }
                return true;
            }

            let Actor = React.createClass({
                render: function () {
                    return (
                            <tr>
                                <td onClick={e => this.props.onItemClick(this.props.name)}>{this.props.name}</td>
                            </tr>
                    );
                }
            });

            let CharacterItem = React.createClass({
                render: function () {
                    return (
                            <tr title={this.props.name}>
                                <td>{truncate(this.props.actor.name, 25)}</td>
                                <td>{truncate(this.props.name, 25)}</td>
                                <td>
                                </td>
                                <td>
                                    <a href="#"
                                       onClick={(e) => {
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

            /**
             * @propFunctions: onDeleteClick, onItemClick
             * */

            let ActorCharacterMapper = React.createClass({
                getInitialState: function () {
                    return {
                        movieId: ${requestScope.movie.getId()},
                        characters: [
                            <jstl:forEach items="${requestScope.characterList}" var="character">
                            {
                                id: ${character.id},
                                name: '${character.name}',
                                movieId: '${character.movieId}',
                                actor: {name: '${character.actor.name}', id: ${character.actor.id}},
                            },
                            </jstl:forEach>
                        ],
                        results: [],
                        offset: 0,
                    }
                },
                getDefaultProps: function () {
                    return {
                        limit: 3,
                        contextPath: '',
                        url: '',
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
                    let url = '${pageContext.request.contextPath}/actor/search';
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
                                // no results
                                if (data.length === 0) {
                                    Materialize.toast('End of search!', 2000);
                                    return;
                                }
                                // add results
                                self.setState(ps => {
                                    return {results: data};
                                });
                                $(self.refs.results).show();
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
                            self.setState({results: []});
                            this.fetchNextBatch(e.target.value);
                            break;
                        // Escape key
                        case 27:
                            $(this.refs.results).hide();
                            break;
                        // Page Up key
                        case 33:
                            this.fetchPreviousBatch(this.refs.pattern.value);
                            break;
                        // Page Down key
                        case 34:
                            this.fetchNextBatch(this.refs.pattern.value);
                            break;
                        default:
                            break;
                    }
                },
                selectActor: function (text) {
                    this.refs.pattern.value = text;
                    $(this.refs.results).hide();

                },
                createCharacter: function () {
                    let self = this;
                    // validation
                    if (!isCharacterValid(this.refs.pattern.value, this.refs.characterName.value)) {
                        return;
                    }
                    // ajax call
                    $.ajax({
                        url: '${pageContext.request.contextPath}/character/create',
                        type: 'POST',
                        data: {
                            actorName: this.refs.pattern.value,
                            characterName: this.refs.characterName.value,
                            movieId: this.state.movieId
                        },
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                let data = json.data;
                                self.setState((prevState, props) => {
                                    prevState.characters.push(data);
                                    return prevState;
                                });
                                $(self.refs.createCharacterForm).find('input').val('');
                                self.setState(ps => {
                                    return {results: []};
                                });
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                deleteCharacter: function (id) {
                    let self = this;
                    $.ajax({
                        url: '${pageContext.request.contextPath}/character/delete',
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
                                    prevState.characters.forEach((character, i) => {
                                        if (character.id === id) {
                                            delIndex = i;
                                        }
                                    });
                                    prevState.characters.splice(delIndex, 1);
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
                    let results = this.state.results.map(actor => {
                        return <Actor
                                key={actor.id}
                                id={actor.id}
                                name={actor.name}
                                onItemClick={this.selectActor}

                        />;
                    });
                    let characters = this.state.characters.map(c => {
                        return <CharacterItem key={c.id}
                                              id={c.id}
                                              name={c.name}
                                              actor={c.actor}
                                              onDeleteClick={this.deleteCharacter}
                        />;
                    });
                    return (
                            <div>
                                <table className="bordered highlight">
                                    <thead>
                                    <tr className="create-character-form"
                                        ref="createCharacterForm">
                                        <td>
                                            <input onKeyDown={this.onRegexInputKeyDown}
                                                   ref="pattern"
                                                   placeholder="Actor" type="text"/>
                                        </td>
                                        <td>
                                            <input type="text"
                                                   ref="characterName"
                                                   name="characterName"
                                                   placeholder="Character Name"
                                                   defaultValue=""/>
                                        </td>
                                        <td>
                                            <button onClick={this.createCharacter}
                                                    className="btn-floating waves-effect waves-light green">
                                                <i className="material-icons">add</i>
                                            </button>
                                        </td>
                                    </tr>
                                    </thead>
                                </table>
                                <div ref="results" className="autocomplete-result-div">
                                    <button className="btn btn-flat"
                                            onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                            className="material-icons">keyboard_arrow_left</i></button>
                                    <button className="btn btn-flat"
                                            onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                            className="material-icons">keyboard_arrow_right</i></button>
                                    <span>Or use PageUp | PageDown to navigate</span>
                                    <table className="white highlight bordered">
                                        <tbody>{results}</tbody>
                                    </table>
                                </div>
                                <table className="highlight centered striped">
                                    <thead>
                                    <tr>
                                        <th>Actor Name</th>
                                        <th>Character Name</th>
                                    </tr>
                                    </thead>
                                    <tbody>{characters}</tbody>
                                </table>
                            </div>
                    );
                },
                componentDidMount: function () {
                    $(this.refs.results).hide();
                },
            });

            let GenreSearch = React.createClass({
                render: function () {
                    return (
                            <tr>
                                <td onClick={e => this.props.onItemClick(this.props.name)}>{this.props.name}</td>
                            </tr>
                    );
                }
            });

            let GenreItem = React.createClass({
                render: function () {
                    return (
                            <tr title={this.props.name}>
                                <td>{truncate(this.props.name, 25)}</td>
                                <td>
                                </td>
                                <td>
                                </td>
                                <td>
                                    <a href="#"
                                       onClick={(e) => {
                                           this.props.onDeleteClick(this.props.id, this.props.movieId)
                                       }}
                                       className="btn-floating waves-effect waves-light red">
                                        <i className="material-icons">remove</i>
                                    </a>
                                </td>
                            </tr>
                    );
                }
            });


            /**
             * @propFunctions: onDeleteClick, onItemClick
             * */
            let GenreList = React.createClass({
                getInitialState: function () {
                    return {
                        movieId: ${requestScope.movie.getId()},
                        genres: [
                            <jstl:forEach items="${requestScope.genreList}" var="genre">
                            {
                                id: ${genre.id},
                                name: '${genre.name}',

                            },
                            </jstl:forEach>
                        ],
                        results: [],
                        offset: 0,
                    }
                },
                getDefaultProps: function () {
                    return {
                        limit: 3,
                        contextPath: '',
                        url: '',
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
                    let url = '${pageContext.request.contextPath}/genre/searchformovie';
                    let self = this;
                    $.ajax({
                        url: url,
                        type: 'GET',
                        data: {
                            pattern: pattern,
                            offset: self.state.offset,
                            limit: self.props.limit,
                            movieId: this.state.movieId
                        },
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                let data = json.data;
                                self._incrementOffset(data.length);
                                // no results
                                if (data.length === 0) {
                                    Materialize.toast('End of search!', 2000);
                                    return;
                                }
                                // add results
                                self.setState(ps => {
                                    return {results: data};
                                });
                                $(self.refs.results).show();
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
                            self.setState({results: []});
                            this.fetchNextBatch(e.target.value);
                            break;
                        // Escape key
                        case 27:
                            $(this.refs.results).hide();
                            break;
                        // Page Up key
                        case 33:
                            this.fetchPreviousBatch(this.refs.pattern.value);
                            break;
                        // Page Down key
                        case 34:
                            this.fetchNextBatch(this.refs.pattern.value);
                            break;
                        default:
                            break;
                    }
                },
                selectGenre: function (text) {
                    this.refs.pattern.value = text;
                    $(this.refs.results).hide();

                },
                createGenre: function () {
                    let self = this;
                    // validation

                    if (!isGenreValid(this.refs.pattern.value)) {
                        return;
                    }

                    // ajax call
                    $.ajax({
                        url: '${pageContext.request.contextPath}/genre/create',
                        type: 'POST',
                        data: {
                            genreName: this.refs.pattern.value,
                            movieId: this.state.movieId
                        },
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                let data = json.data;
                                self.setState((prevState, props) => {
                                    prevState.genres.push(data);
                                    return prevState;
                                });
                                $(self.refs.createGenreForm).find('input').val('');
                                self.setState(ps => {
                                    return {results: []};
                                });
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                deleteGenre: function (id, movieId) {
                    let self = this;
                    $.ajax({
                        url: '${pageContext.request.contextPath}/genre/delete',
                        type: 'GET',
                        data: {id: id, movieId: movieId},
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                self.setState((prevState, props) => {
                                    let delIndex = -1;
                                    prevState.genres.forEach((genre, i) => {
                                        if (genre.id === id) {
                                            delIndex = i;
                                        }
                                    });
                                    prevState.genres.splice(delIndex, 1);
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
                    let results = this.state.results.map(genre => {
                        return <GenreSearch
                                key={genre.id}
                                id={genre.id}
                                name={genre.name}
                                onItemClick={this.selectGenre}

                        />;
                    });
                    let genres = this.state.genres.map(c => {
                        return <GenreItem key={c.id}
                                          id={c.id}
                                          name={c.name}
                                          movieId={this.state.movieId}
                                          onDeleteClick={this.deleteGenre}
                        />;
                    });
                    return (
                            <div>
                                <table className="bordered highlight">
                                    <thead>
                                    <tr className="create-genre-form"
                                        ref="createGenreForm">
                                        <td>
                                            <input onKeyDown={this.onRegexInputKeyDown}
                                                   ref="pattern"
                                                   placeholder="Genre Name" type="text"/>
                                        </td>

                                        <td>
                                            <button onClick={this.createGenre}
                                                    className="btn-floating waves-effect waves-light green">
                                                <i className="material-icons">add</i>
                                            </button>
                                        </td>
                                    </tr>
                                    </thead>
                                </table>
                                <div ref="results" className="autocomplete-result-div">
                                    <button className="btn btn-flat"
                                            onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                            className="material-icons">keyboard_arrow_left</i></button>
                                    <button className="btn btn-flat"
                                            onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                            className="material-icons">keyboard_arrow_right</i></button>
                                    <span>Or use PageUp | PageDown to navigate</span>
                                    <table className="white highlight bordered">
                                        <tbody>{results}</tbody>
                                    </table>
                                </div>
                                <table className="highlight centered striped">
                                    <thead>
                                    <tr>
                                        <th>Genre Name</th>
                                    </tr>
                                    </thead>
                                    <tbody>{genres}</tbody>
                                </table>
                            </div>
                    );
                },
                componentDidMount: function () {
                    $(this.refs.results).hide();
                },
            });

            ReactDOM.render(<ActorCharacterMapper/>, document.getElementById('characterApp'));
            ReactDOM.render(<GenreList/>, document.getElementById('genreApp'));
        </script>
        <div class="container">
            <div class="row">
                <div class="col s12">
                    <h2 class="blue-text">${requestScope.movie.getName()}</h2>
                    <div class="divider"></div>
                </div>
                <div class="col s6">
                    <h5>Cast</h5>
                    <div id="characterApp"></div>
                </div>
                <div class="col s6">
                    <h5>Genres</h5>
                    <div id="genreApp"></div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/movieowner/movieeditor">Back</a>
        </div>
    </jsp:body>
</m:base>