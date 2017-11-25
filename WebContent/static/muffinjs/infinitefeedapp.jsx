let fromNow = function (localDateTime) {
    let p = moment();
    p.year(localDateTime.date.year).month(localDateTime.date.month - 1).date(localDateTime.date.day);
    p.hour(localDateTime.time.hour).minute(localDateTime.time.minute).second(localDateTime.time.second);
    return p.fromNow();
};

let SeekResponse = React.createClass({
    getInitialState: function () {
        return {
            approvalStatus: 0,
        }
    },
    getDefaultProps: function () {
        return {
            seekedMuffId: 0,
            contextPath: '',
            toggleUrl: '/seek/response/approval/toggle',
            inSessionMuffId: 0,
        }
    },
    toggleApprovalLock: false,
    toggleApproval: function () {
        let self = this;
        let seekResponseId = this.props.data.id;
        let url = this.props.toggleUrl;
        if (this.toggleApprovalLock) {
            return;
        }
        this.toggleApprovalLock = true;
        $.ajax({
            url: url,
            type: 'GET',
            data: {seekResponseId: seekResponseId},
            success: function (r) {
                self.toggleApprovalLock = false;
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self.setState({approvalStatus: data});
                }
            },
            error: function (data) {
                self.toggleApprovalLock = false;
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        let muffCanToggle = this.props.inSessionMuffId === this.props.seekedMuffId;
        let toggleSeekResponseBtn = (
            <i className="material-icons"
               onClick={this.toggleApproval}
               style={{
                   cursor: 'pointer',
                   float: 'right',
                   fontSize: '16px',
                   lineHeight: '32px',
                   paddingLeft: '8px',
               }}
            >{this.state.approvalStatus === 1 ? 'done' : 'check_box_outline_blank'}</i>
        );
        // if muff can toggle use state otherwise props
        let backgroundColor;
        if (muffCanToggle) {
            backgroundColor = this.state.approvalStatus === 1 ? '#66bb6a' : '#e4e4e4'
        } else {
            backgroundColor = this.props.data.approvalStatus === 1 ? '#66bb6a' : '#e4e4e4'
        }
        return (
            <div className="collection-item">
                <div className="secondary-content">
                    <div className="black-text">
                        {this.props.data.muff.name}
                    </div>
                    <div className="pink-text">
                        @{this.props.data.muff.handle}
                    </div>
                </div>
                <div className="chip waves-effect black-text"
                     style={{backgroundColor: backgroundColor}}>
                    {this.props.data.movieName}
                    {muffCanToggle ? toggleSeekResponseBtn : ''}
                </div>
                <div>
                    {this.props.data.text}
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.setState((ps, pp) => {
            return {
                approvalStatus: pp.data.approvalStatus
            }
        });
    }
});

/**
 * @propFunctions: onResultClick
 * */
let MovieSearchResult = React.createClass({
    render: function () {
        return (
            <div className="collection-item" onClick={e => this.props.onResultClick(this.props.id, this.props.name)}>
                <div>{this.props.name}</div>
            </div>
        );
    }
});

/**
 * @propFunctions: onCreateSeekResponse
 * */
let CreateSeekResponseApp = React.createClass({
    giveSeekResponseLock: false,
    fetchNextBatchLock: false,
    getInitialState: function () {
        return {
            results: [],
            offset: 0,
        }
    },
    getDefaultProps: function () {
        return {
            limit: 5,
            seekId: undefined,
            contextPath: '',
            searchUrl: '/movie/search',
            createSeekResponseUrl: '/seek/response/create',
        }
    },
    _resetOffset: function () {
        this.state.offset = 0;
    },
    _incrementOffset: function (fetchedDataLength) {
        this.state.offset += fetchedDataLength;
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
        let self = this;
        let url = this.props.contextPath + this.props.searchUrl;
        if (this.fetchNextBatchLock) {
            return;
        }
        this.fetchNextBatchLock = true;
        $.ajax({
            url: url,
            type: 'GET',
            data: {pattern: pattern, offset: self.state.offset, limit: self.props.limit},
            success: function (r) {
                self.fetchNextBatchLock = false;
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
                self.fetchNextBatchLock = false;
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
                self.setState(ps => {
                    return {results: []};
                });
                this.fetchNextBatch(e.target.value);
                break;
            // Escape key
            case 27:
                $(this.refs.results).hide();
                break;
            default:
                break;
        }
    },
    selectResult: function (id, name) {
        this.refs.pattern.value = name;
        $(this.refs.results).hide();
    },
    isSeekResponseValid: function (seekId, movieName, text) {
        if (seekId === undefined) {
            Materialize.toast('Seek Id not initialized properly', 2000);
            return false;
        }
        if (movieName === undefined || movieName === '') {
            Materialize.toast('Please select a movie to recommend!', 2000);
            return false;
        }
        return true;
    },
    giveSeekResponse: function () {
        let self = this;
        let url = this.props.contextPath + this.props.createSeekResponseUrl;
        let seekId = this.props.seekId;
        let movieName = this.refs.pattern.value;
        let text = this.refs.text.value;
        if (!this.isSeekResponseValid(seekId, movieName, text)) {
            return;
        }
        if (this.giveSeekResponseLock) {
            return;
        }
        this.giveSeekResponseLock = true;
        $.ajax({
            url: url,
            type: 'GET',
            data: {movieName: movieName, seekId: seekId, text: text},
            success: function (r) {
                self.giveSeekResponseLock = false;
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self.props.onCreateSeekResponse(data);
                    $(self.refs.form).find('input, textarea').val('');
                }
            },
            error: function (data) {
                self.giveSeekResponseLock = false;
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        let results = this.state.results.map(result => {
            return <MovieSearchResult
                key={result.id}
                id={result.id}
                name={result.name}
                onResultClick={this.selectResult}
            />;
        });
        return (
            <div className="row" ref="form">
                <div className="col s12">
                    <button className="btn btn-flat right" onClick={this.giveSeekResponse}>Recommend
                    </button>
                </div>
                <div className="col s12">
                    <input onKeyDown={this.onRegexInputKeyDown}
                           ref="pattern"
                           placeholder="Recommend a movie" type="text" className="col s12"/>
                    <div className="col s12" ref="results">
                        <div className="collection with-header">
                            <div className="collection-header"><span className="flow-text">Results</span>
                                <span className="right">
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_left</i></button>
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_right</i></button>
                            </span>
                            </div>
                            {results}
                        </div>
                    </div>
                </div>
                <div className="col s12">
                    <textarea className="materialize-textarea" defaultValue=""
                              placeholder="Say something about it"
                              ref="text"
                              style={{margin: '0px', padding: '0px'}}>
                    </textarea>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        $(this.refs.results).hide();
    },
});
/**
 * @propFunctions: requestGoToBottom
 * */
let Seek = React.createClass({
    seekResponseFetchLock: false,
    checkForNewResponseLock: false,
    getInitialState: function () {
        return {
            fromTimestamp: '',
            seekResponseLastSeen: moment().format('YYYY-MM-DD HH:mm:ss'),
            seekResponseOffset: 0,
            seekResponseHashMap: {},
            seekResponses: [],
        }
    },
    getDefaultProps: function () {
        return {
            data: {},
            inSessionMuffId: 0,
            contextPath: '',
            checkForNewResponseUrl: '/seek/response/check/forNew',
            respondedMuffIdsFetchUrl: '/seek/response/fetch/respondedMuffIdsOfSeek',
            seekResponseFetchUrl: '/seek/response/fetch/seek',
            seekResponseFetchParam: 0,
            seekResponseFetchLimit: 0,
            timeIntervalForNewResponseCheck: 10000, // ms
        };
    },
    refreshFromNowTS: function () {
        $(this.refs.fromNow).html(fromNow(this.props.data.timestamp));
    },
    onCreateSeekResponse: function (seekResponse) {
        this.fetchNextSeekResponseBatch();
        $(this.refs.createSeekResponseAppDiv).hide();
    },
    fetchNextSeekResponseBatch: function (shouldToastIfNoMoreFeed) {
        let self = this;
        let url = this.props.contextPath + this.props.seekResponseFetchUrl;
        let limit = this.props.seekResponseFetchLimit;
        let param = this.props.seekResponseFetchParam;
        let offset = this.state.seekResponseOffset;
        let lastSeen = this.state.seekResponseLastSeen;
        let requestTimeStamp = moment().format('YYYY-MM-DD HH:mm:ss');
        $(this.refs.loadMore).prop('disabled', true);
        if (this.seekResponseFetchLock) {
            return;
        }
        this.seekResponseFetchLock = true;
        $.ajax({
            url: url,
            type: 'GET',
            data: {
                id: param,
                offset: offset,
                limit: limit,
                lastSeen: lastSeen,
            },
            success: function (r) {
                self.seekResponseFetchLock = false;
                $(self.refs.loadMore).prop('disabled', false);
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let fetchedData = json.data;
                    // no results
                    if (fetchedData.length === 0) {
                        if (shouldToastIfNoMoreFeed !== true) {
                            Materialize.toast('That\'s it', 2000);
                        }
                        return;
                    }
                    // add results
                    self.setState(prevState => {
                        let seekResponses = prevState.seekResponses;
                        let seekResponseHashMap = prevState.seekResponseHashMap;
                        let noNewSeekResponses = 0;
                        fetchedData.forEach((fetchedItem, i) => {
                            // new fetchedItem
                            if (seekResponseHashMap[fetchedItem.id] === undefined) {
                                seekResponseHashMap[fetchedItem.id] = seekResponses.length;
                                seekResponses.push(fetchedItem);
                                noNewSeekResponses++;
                            }
                            // old fetchedItem
                            else {
                                let existingFeedIndex = seekResponseHashMap[fetchedItem.id];
                                // invalidate old one
                                seekResponses[existingFeedIndex] = undefined;
                                // add new one at end
                                seekResponseHashMap[fetchedItem.id] = seekResponses.length;
                                seekResponses.push(fetchedItem);
                            }
                        });
                        let seekResponseOffset = prevState.seekResponseOffset + noNewSeekResponses;
                        // return
                        let retObject = {
                            seekResponse: seekResponses,
                        };
                        retObject.seekResponseLastSeen = requestTimeStamp;
                        retObject.seekResponseOffset = seekResponseOffset;
                        retObject.seekResponseHashMap = seekResponseHashMap;
                        return retObject;
                    });
                }
            },
            error: function (data) {
                self.seekResponseFetchLock = false;
                $(self.refs.loadMore).prop('disabled', false);
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    checkIfAlreadyResponded: function () {
        let self = this;
        let seekId = this.props.data.id;
        let url = this.props.contextPath + this.props.respondedMuffIdsFetchUrl;
        $.ajax({
            url: url,
            type: 'GET',
            data: {
                seekId: seekId,
            },
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let respondedMuffIds = json.data;
                    let notYetResponded = true;
                    respondedMuffIds.forEach(muffId => {
                        if (muffId === self.props.inSessionMuffId) {
                            notYetResponded = false;
                        }
                    });
                    if (notYetResponded) {
                        $(self.refs.createSeekResponseAppDiv).fadeIn(0);
                    }
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    checkForNewResponses: function () {
        let self = this;
        let seekId = this.props.data.id;
        let lastSeen = this.state.seekResponseLastSeen;
        let url = this.props.contextPath + this.props.checkForNewResponseUrl;
        if (this.checkForNewResponseLock) {
            return;
        }
        this.checkForNewResponseLock = true;
        $.ajax({
            url: url,
            type: 'GET',
            data: {
                seekId: seekId,
                lastSeen: lastSeen,
            },
            success: function (r) {
                self.checkForNewResponseLock = false;
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let newResponsesExist = json.data;
                    if (newResponsesExist) {
                        self.fetchNextSeekResponseBatch();
                        self.props.requestGoToBottom(self.props.data.id);
                    }
                }
            },
            error: function (data) {
                self.checkForNewResponseLock = false;
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        let responses = [];
        for (let i = 0; i < this.state.seekResponses.length; i++) {
            let response = this.state.seekResponses[i];
            if (response === undefined) {
                continue;
            }
            responses.push(
                <SeekResponse
                    inSessionMuffId={this.props.inSessionMuffId}
                    seekedMuffId={this.props.data.muff.id}
                    contextPath={this.props.contextPath}
                    key={response.id}
                    data={response}
                />
            );
        }
        return (
            <div ref="feedItem">
                <div className="card seek hoverable yellow lighten-4"
                     onMouseEnter={e => this.refreshFromNowTS()}>
                    <div className="card-content">
                        <div>{this.props.data.muff.name} <span
                            className="pink-text">@{this.props.data.muff.handle}</span></div>
                        <div className="blue-text" ref="fromNow">
                        </div>
                        <br/>
                        <div ref="genres">{
                            this.props.data.genres.map(genre => {
                                return <span key={genre.id} className="teal white-text  badge">{genre.name}</span>
                            })
                        }</div>
                        <br/>
                        <div className="flow-text">{this.props.data.text}</div>
                    </div>
                    <div className="card" ref="createSeekResponseAppDiv" style={{display: 'none'}}>
                        <div className="card-content" style={{padding: '5px'}}>
                            <CreateSeekResponseApp
                                contextPath={this.props.contextPath}
                                seekId={this.props.data.id}
                                onCreateSeekResponse={this.onCreateSeekResponse}
                            />
                        </div>
                    </div>
                    <div className="collection" style={{margin: '0px'}}>
                        {responses}
                    </div>
                    <div>
                        <button className="btn btn-flat right-align" ref="loadMore"
                                onClick={e => this.fetchNextSeekResponseBatch()}>
                            <i className="material-icons">more_horiz</i>
                        </button>
                    </div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        $(this.refs.feedItem).fadeOut(0);
        // refresh timestamp
        this.refreshFromNowTS();
        // decide whether or not to be able to give response
        if (this.props.data.muff.id === this.props.inSessionMuffId) {
            $(this.refs.createSeekResponseAppDiv).fadeOut(0);
        } else {
            this.checkIfAlreadyResponded();
        }
        // get first batch
        this.fetchNextSeekResponseBatch(true);
        $(this.refs.feedItem).fadeIn(1000);
        // start periodic polling with a random delay to reduce traffic bursts
        setTimeout(p => {
            setInterval(e => {
                this.checkForNewResponses();
            }, this.props.timeIntervalForNewResponseCheck);
        }, Math.random() * this.props.timeIntervalForNewResponseCheck);
    },
});

let Review = React.createClass({
    getInitialState: function () {
        return {
            fromLastModified: '',
        }
    },
    refreshFromNowTS: function () {
        $(this.refs.fromNow).html(fromNow(this.props.data.lastModified));
    },
    render: function () {
        return (
            <div className="card review hoverable" ref="feedItem"
                 style={{backgroundColor: 'whitesmoke'}}
                 onMouseEnter={e => this.refreshFromNowTS()}>
                <div className="card-content">
                    <div>{this.props.data.muff.name} <span className="pink-text">@{this.props.data.muff.handle}</span>
                    </div>
                    <div>{this.props.data.movieName}</div>
                    <div className="blue-text" ref="fromNow">
                    </div>
                    <br/>
                    <div className="red-text">{this.props.data.rating}</div>
                    <div className="flow-text">{this.props.data.text}</div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.refreshFromNowTS();
        $(this.refs.feedItem).fadeOut(0).fadeIn(1000);
    },
});

let FEED_TYPES = {
    REVIEW: 'review',
    SEEK: 'seek',
    AUTOMATED_MOVIE_SUGGESTION: 'automatedMovieSuggestion',
    AUTOMATED_MUFF_SUGGESTION: 'automatedMuffSuggestion',
};
let FEED_POSTFIXES = {
    STATE: {
        OFFSET: 'Offset',
        FEED_HASH_MAP: 'FeedHashMap',
        LAST_SEEN: 'LastSeen',
    },
    PROPS: {
        FETCH_URL: 'FetchUrl',
        FETCH_PARAM: 'FetchParam',
        FETCH_LIMIT: 'FetchLimit',
        IS_ENABLED: 'IsEnabled',
    },
};
// todo: handle multiple types
// todo: clock sync
/**
 * This buffering model can handle
 * fetching newly created items, updated existing items as long as the a timestamp is maintained
 * But it becomes inconsistent when items can be deleted, roughly speaking some items can never be fetched due to offset problems
 * */
window.InfiniteFeedApp = React.createClass({
    feedFetchLock: false,
    getInitialState: function () {
        let ts = moment().format('YYYY-MM-DD HH:mm:ss');
        let initialState = {
            feed: [],
        };
        // REVIEW
        initialState[FEED_TYPES.REVIEW + FEED_POSTFIXES.STATE.LAST_SEEN] = ts;
        initialState[FEED_TYPES.REVIEW + FEED_POSTFIXES.STATE.OFFSET] = 0;
        initialState[FEED_TYPES.REVIEW + FEED_POSTFIXES.STATE.FEED_HASH_MAP] = {};
        // SEEK
        initialState[FEED_TYPES.SEEK + FEED_POSTFIXES.STATE.LAST_SEEN] = ts;
        initialState[FEED_TYPES.SEEK + FEED_POSTFIXES.STATE.OFFSET] = 0;
        initialState[FEED_TYPES.SEEK + FEED_POSTFIXES.STATE.FEED_HASH_MAP] = {};
        // AMovSug
        initialState[FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION + FEED_POSTFIXES.STATE.LAST_SEEN] = ts;
        initialState[FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION + FEED_POSTFIXES.STATE.OFFSET] = 0;
        initialState[FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION + FEED_POSTFIXES.STATE.FEED_HASH_MAP] = {};
        // AMuffSug
        initialState[FEED_TYPES.AUTOMATED_MUFF_SUGGESTION + FEED_POSTFIXES.STATE.LAST_SEEN] = ts;
        initialState[FEED_TYPES.AUTOMATED_MUFF_SUGGESTION + FEED_POSTFIXES.STATE.OFFSET] = 0;
        initialState[FEED_TYPES.AUTOMATED_MUFF_SUGGESTION + FEED_POSTFIXES.STATE.FEED_HASH_MAP] = {};
        return initialState;
    },
    getDefaultProps: function () {
        let defaultProps = {
            contextPath: '',
            inSessionMuffId: 0,
        };
        // REVIEW
        defaultProps[FEED_TYPES.REVIEW + FEED_POSTFIXES.PROPS.FETCH_LIMIT] = 5;
        defaultProps[FEED_TYPES.REVIEW + FEED_POSTFIXES.PROPS.FETCH_PARAM] = 0;
        defaultProps[FEED_TYPES.REVIEW + FEED_POSTFIXES.PROPS.FETCH_URL] = '';
        defaultProps[FEED_TYPES.REVIEW + FEED_POSTFIXES.PROPS.IS_ENABLED] = true;
        // SEEK
        defaultProps[FEED_TYPES.SEEK + FEED_POSTFIXES.PROPS.FETCH_LIMIT] = 3;
        defaultProps[FEED_TYPES.SEEK + FEED_POSTFIXES.PROPS.FETCH_PARAM] = 0;
        defaultProps[FEED_TYPES.SEEK + FEED_POSTFIXES.PROPS.FETCH_URL] = '';
        defaultProps[FEED_TYPES.SEEK + FEED_POSTFIXES.PROPS.IS_ENABLED] = true;
        // AMovSug
        defaultProps[FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION + FEED_POSTFIXES.PROPS.FETCH_LIMIT] = 3;
        defaultProps[FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION + FEED_POSTFIXES.PROPS.FETCH_PARAM] = 0;
        defaultProps[FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION + FEED_POSTFIXES.PROPS.FETCH_URL] = '/suggestion/fetch/movies';
        defaultProps[FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION + FEED_POSTFIXES.PROPS.IS_ENABLED] = true;
        // AMuffSug
        defaultProps[FEED_TYPES.AUTOMATED_MUFF_SUGGESTION + FEED_POSTFIXES.PROPS.FETCH_LIMIT] = 3;
        defaultProps[FEED_TYPES.AUTOMATED_MUFF_SUGGESTION + FEED_POSTFIXES.PROPS.FETCH_PARAM] = 0;
        defaultProps[FEED_TYPES.AUTOMATED_MUFF_SUGGESTION + FEED_POSTFIXES.PROPS.FETCH_URL] = '/suggestion/fetch/muffs';
        defaultProps[FEED_TYPES.AUTOMATED_MUFF_SUGGESTION + FEED_POSTFIXES.PROPS.IS_ENABLED] = true;
        return defaultProps;
    },
    fetchNextFeedBatch: function (type) {
        let isEnabled = this.props[type + FEED_POSTFIXES.PROPS.IS_ENABLED];
        if (!isEnabled) {
            return;
        }
        let self = this;

        let url = this.props.contextPath + this.props[type + FEED_POSTFIXES.PROPS.FETCH_URL];
        let limit = this.props[type + FEED_POSTFIXES.PROPS.FETCH_LIMIT];
        let param = this.props[type + FEED_POSTFIXES.PROPS.FETCH_PARAM];

        let offsetKey = type + FEED_POSTFIXES.STATE.OFFSET;
        let offset = this.state[offsetKey];

        let lastSeenKey = type + FEED_POSTFIXES.STATE.LAST_SEEN;
        let lastSeen = this.state[lastSeenKey];

        let hashMapKey = type + FEED_POSTFIXES.STATE.FEED_HASH_MAP;
        let requestTimeStamp = moment().format('YYYY-MM-DD HH:mm:ss');
        if (this.feedFetchLock) {
            return;
        }
        this.feedFetchLock = true;
        $.ajax({
            url: url,
            type: 'GET',
            data: {
                id: param,
                offset: offset,
                limit: limit,
                lastSeen: lastSeen,
            },
            success: function (r) {
                self.feedFetchLock = false;
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                    // Signal RR Feed Fetch handler
                    self.onRRFeedFetch(type, 0);
                }
                else {
                    let fetchedData = json.data;
                    // Signal RR Feed Fetch handler
                    self.onRRFeedFetch(type, fetchedData.length);
                    // no results
                    if (fetchedData.length === 0) {
                        return;
                    }
                    // add results
                    self.setState(prevState => {
                        let feed = prevState.feed;
                        let _T_HashMap = prevState[hashMapKey];
                        let noNew_T_s = 0;
                        fetchedData.forEach((fetchedItem, i) => {
                            // new fetchedItem
                            if (_T_HashMap[fetchedItem.id] === undefined) {
                                _T_HashMap[fetchedItem.id] = feed.length;
                                feed.push({
                                    type: type,
                                    data: fetchedItem
                                });
                                noNew_T_s++;
                            }
                            // old fetchedItem
                            else {
                                let existingFeedIndex = _T_HashMap[fetchedItem.id];
                                // invalidate old one
                                feed[existingFeedIndex] = undefined;
                                // add new one at end
                                _T_HashMap[fetchedItem.id] = feed.length;
                                feed.push({
                                    type: type,
                                    data: fetchedItem
                                });
                            }
                        });
                        let _T_Offset = prevState[offsetKey] + noNew_T_s;
                        // return
                        let retObject = {
                            feed: feed,
                        };
                        retObject[lastSeenKey] = requestTimeStamp;
                        retObject[offsetKey] = _T_Offset;
                        retObject[hashMapKey] = _T_HashMap;
                        return retObject;
                    });
                }
            },
            error: function (data) {
                self.feedFetchLock = false;
                // Signal RR Feed Fetch handler
                self.onRRFeedFetch(type, 0);
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    RRFeedFetchState: {
        lock: false,
        typesFetchedBitmap: {},
        noFeedItemsFetchedInOneCycle: 0,
        reset: function () {
            this.noFeedItemsFetchedInOneCycle = 0;
            this.typesFetchedBitmap = {};
        }
    },
    onRRFeedFetch: function (type, noFeedItems) {
        // update state
        this.RRFeedFetchState.typesFetchedBitmap[type] = true;
        this.RRFeedFetchState.noFeedItemsFetchedInOneCycle += noFeedItems;
        // get any un-fetched feed item type
        let newType = undefined;
        for (let key in FEED_TYPES) {
            if (FEED_TYPES.hasOwnProperty(key)) {
                // if this feed is not fetched in this cycle
                if (this.RRFeedFetchState.typesFetchedBitmap[FEED_TYPES[key]] !== true) {
                    // if feed is the automated suggestions then control its probability
                    if (FEED_TYPES[key] === FEED_TYPES.AUTOMATED_MUFF_SUGGESTION
                        || FEED_TYPES[key] === FEED_TYPES.AUTOMATED_MUFF_SUGGESTION) {
                        if (Math.random() > 0.2) {
                            continue
                        }
                    }
                    newType = FEED_TYPES[key];
                    break;
                }
            }
        }
        if (newType === undefined) {
            // end the cycle
            this.RRFeedFetchState.lock = false;
            if (this.RRFeedFetchState.noFeedItemsFetchedInOneCycle === 0) {
                Materialize.toast('End of feed!', 2000);
            }
        } else {
            // fetch new feed item
            this.fetchNextFeedBatch(newType);
        }
    },
    startRRFeedFetch: function () {
        if (this.RRFeedFetchState.lock) {
            Materialize.toast('Fetching in progress', 2000);
            return;
        }
        this.RRFeedFetchState.lock = true;
        this.RRFeedFetchState.reset();
        this.fetchNextFeedBatch(FEED_TYPES.SEEK);
    },
    bringFeedItemToBottom: function (type, feedItemId) {
        let isEnabled = this.props[type + FEED_POSTFIXES.PROPS.IS_ENABLED];
        if (!isEnabled) {
            Materialize.toast(type + ' feed is disabled', 3000);
            return;
        }
        let hashMapKey = type + FEED_POSTFIXES.STATE.FEED_HASH_MAP;
        this.setState(prevState => {
            let feed = prevState.feed;
            let _T_HashMap = prevState[hashMapKey];

            let existingFeedIndex = _T_HashMap[feedItemId];
            if (existingFeedIndex === feed.length - 1) {
                return {};
            }
            // invalidate old one
            let feedItem = feed.splice(existingFeedIndex, 1)[0];
            feed.splice(existingFeedIndex, 0, undefined);
            // add new one at end
            _T_HashMap[feedItemId] = feed.length;
            feed.push(feedItem);
            // return
            let retObject = {
                feed: feed,
            };
            retObject[hashMapKey] = _T_HashMap;
            return retObject;
        });
    },
    render: function () {
        let HTMLFeed = [];
        for (let i = 0; i < this.state.feed.length; ++i) {
            let feedItem = this.state.feed[i];
            if (feedItem === undefined) {
                continue;
            }
            switch (feedItem.type) {
                case FEED_TYPES.REVIEW:
                    HTMLFeed.push(
                        <Review
                            key={FEED_TYPES.REVIEW + '-' + feedItem.data.id}
                            data={feedItem.data}/>
                    );
                    break;
                case FEED_TYPES.SEEK:
                    HTMLFeed.push(
                        <Seek
                            key={FEED_TYPES.SEEK + '-' + feedItem.data.id}
                            data={feedItem.data}
                            contextPath={this.props.contextPath}
                            inSessionMuffId={this.props.inSessionMuffId}
                            seekResponseFetchParam={feedItem.data.id}
                            seekResponseFetchLimit={3}
                            requestGoToBottom={seekId => this.bringFeedItemToBottom(FEED_TYPES.SEEK, seekId)}
                        />
                    );
                    break;
                case FEED_TYPES.AUTOMATED_MOVIE_SUGGESTION:
                    console.log(feedItem);
                    break;
                case FEED_TYPES.AUTOMATED_MUFF_SUGGESTION:
                    console.log(feedItem);
                    break;
                default:
                    HTMLFeed.push(<div>Unknown Element</div>);
                    break;
            }
        }
        return (
            <div style={{marginBottom: '50vh'}}>
                <div ref="feed">
                    {HTMLFeed}
                </div>
                <div className="card">
                    <div className="card-content">
                        <button className="btn btn-flat" onClick={e => this.startRRFeedFetch()}>
                            <i className="material-icons">more_vert</i>
                        </button>
                    </div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        let self = this;
        $(window).scroll(function () {
            if ($(window).scrollTop() + $(window).height() === $(document).height()) {
                self.startRRFeedFetch()
            }
        });
        this.startRRFeedFetch()
    },
});
