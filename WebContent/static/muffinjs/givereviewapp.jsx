let isReviewValid = function (name, rating, review) {
    if (name === '') {
        Materialize.toast('Movie name is empty!', 3000);
        return false;
    }
    if (name.length > 50) {
        Materialize.toast('Movie name must be less than 50 characters!', 3000);
        return false;
    }

    if (isNaN(Number(rating)) && rating !== '') {
        Materialize.toast('Invalid Rating!', 3000);
        return false;
    }
    rating = Number(rating);
    if (rating < 0 || rating > 10) {
        Materialize.toast('Invalid rating!', 3000);
        return false;
    }
    return true;
};

let MovieSearchResult = React.createClass({
    render: function () {
        return (
            <div className="collection-item"
               onClick={e => this.props.onItemClick(this.props.name)}>{this.props.name}</div>
        );
    }
});

window.GiveReviewApp = React.createClass({
    getInitialState: function () {
        return {
            movieSearchResults: [],
            offset: 0,
        }
    },
    getDefaultProps: function () {
        return {
            contextPath: '',
            url: '/review/give',
            limit: 10,
            movieSearchUrl: '/movie/search',
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
        let url = this.props.contextPath + this.props.movieSearchUrl;
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
        this.refs.name.value = text;
        $(this.refs.movieSearchResults).hide();

    },
    addMovieReview: function () {
        // validation
        if (!isReviewValid(this.refs.name.value, this.refs.rating.value, this.refs.textReview.value)) {
            return;
        }
        let self = this;
        // ajax call
        $.ajax({
            url: self.props.contextPath + self.props.url,
            type: 'POST',
            data: {
                name: this.refs.name.value,
                rating: this.refs.rating.value,
                textReview: this.refs.textReview.value
            },
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    Materialize.toast(data, 2000);
                    $(self.refs.form).find('input, textarea').val('');
                    $(self.refs.form).find('input[type=range]').val(0);
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        let movieSearchResults = this.state.movieSearchResults.map(movie => {
            return <MovieSearchResult
                key={movie.id}
                id={movie.id}
                name={movie.name}
                onItemClick={this.selectMovie}
            />;
        });
        return (
            <div ref="form" className="row">
                <div className="input-field col s8">
                    <input type="text" ref="name" placeholder="Name of the movie" onKeyDown={this.onRegexInputKeyDown}
                           defaultValue=""/>
                </div>
                <div className="input-field col s4">
                    <button onClick={this.addMovieReview}
                            className="btn-flat btn right">
                        Give Review
                    </button>
                </div>
                <div className="col s12" ref="movieSearchResults">
                    <div className="collection with-header">
                        <div className="collection-header"><span className="flow-text">Movies</span>
                            <span className="right">
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchPreviousBatch(this.refs.name.value)}><i
                                className="material-icons">keyboard_arrow_left</i></button>
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchNextBatch(this.refs.name.value)}><i
                                className="material-icons">keyboard_arrow_right</i></button>
                            </span>
                        </div>
                        {movieSearchResults}
                    </div>
                </div>
                <div className="input-field col s8">
                    <p className="range-field">
                        <span>Stars</span>
                        <input type="range" min="0" max="10" ref="rating" step="0.1"
                               defaultValue="0"/>
                    </p>
                </div>
                <div className="input-field col s12">
                        <textarea ref="textReview" placeholder="What do you feel about the movie?" defaultValue=""
                                  className="materialize-textarea">
                        </textarea>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        $(this.refs.movieSearchResults).hide();
    },
});
